package app

import org.ergoplatform.appkit._
import org.ergoplatform.appkit.impl.Eip4TokenBuilder
import org.ergoplatform.explorer.client.model.OutputInfo
import scorex.crypto.hash.Sha256
import org.guapswap._
import scorex.util.encode.Base16
import utils.ExplorerApi
import utils.LensUtils.{LILIUM_COLLECTION_ISSUANCE_ERGOTREE_TEMPLATE_HEX, LILIUM_STATE_BOX_ERGOTREE_TEMPLATE_HEX, SKYHARBOR_SALE_BOX_ERGOTREE_TEMPLATE_HEX, getErgoTreeTemplateHex}

import java.nio.charset.Charset

object LensCommands {
  def snapshot(networkType: NetworkType, nodeUrl: String, explorerUrl: String, collectionId: String): Unit = {

    // snapshot based on marketplace type
    // 1. get lilium snapshot based on collection token id
    // - search all boxes with given collection token id, minus the state box
    // 2. get corresponding skyharbor listings (past/present)
    val api: ExplorerApi = ExplorerApi(nodeUrl, explorerUrl)

    var response: Array[OutputInfo] = api.getBoxesByTokenID(collectionId, 0, 100)
    var set: Array[OutputInfo] = Array()
    var offset: Integer = 0

    while (response.nonEmpty) {

      set = set ++ response
      offset += response.length
      response = api.getBoxesByTokenID(collectionId, offset, 100)

    }

    val collectionWithoutState = set.filter(output => {
      val template = getErgoTreeTemplateHex(output.getErgoTree)
      !template.equals(LILIUM_STATE_BOX_ERGOTREE_TEMPLATE_HEX) && !template.equals(LILIUM_COLLECTION_ISSUANCE_ERGOTREE_TEMPLATE_HEX)
    })

    val boxIds = collectionWithoutState.map(output => output.getBoxId)
    val nftBoxes = boxIds.map(id => api.getUnspentBoxesByTokenID(id, 0, 1)(0))

    val marketplaceFiltered = nftBoxes.map(box => {
      val ergotree = box.getErgoTree
      val template = getErgoTreeTemplateHex(ergotree)
      if (template.equals(SKYHARBOR_SALE_BOX_ERGOTREE_TEMPLATE_HEX)) {
        val regValue = box.getAdditionalRegisters.get("R5").renderedValue
        Address.fromPropositionBytes(networkType, Base16.decode(regValue).get).asP2PK().toString()
      } else {
        box.getAddress
      }
    })

    marketplaceFiltered.foreach(add => System.out.println(add))

  }


  def print(ergoClient: ErgoClient, networkType: NetworkType, boxType: String, boxId: String): Unit = {

    ergoClient.execute((ctx: BlockchainContext) => {

      val box = ctx.getDataSource.getBoxById(boxId, false, true)

      if (boxType.equals("eip4issuance")) {
        sigmabuilders.printers.minting_printers.EIP4PictureNFTPrinter.printDecodedBox(box, networkType)
      } else if (boxType.equals("eip24issuer")) {
        sigmabuilders.printers.minting_printers.EIP24IssuerBoxPrinter.printDecodedBox(box, networkType)
      } else if (boxType.equals("eip34issuer")) {
        sigmabuilders.printers.minting_printers.EIP34IssuerBoxPrinter.printDecodedBox(box, networkType)
      } else {
        throw new IllegalArgumentException("invalid box type input")
      }

    })

  }

  def mint(ergoClient: ErgoClient, tokenName: String, tokenDescription: String, tokenContent: String, tokenLink: String, boxId: String, walletAddress: String, mnemonic: String): (String, String) = {

    // Generate blockchain context
    val mintForDummiesTxIds: (String, String) = ergoClient.execute((ctx: BlockchainContext) => {

      // create tx builder
      val issuerCreationTxBuilder: UnsignedTransactionBuilder = ctx.newTxBuilder()

      // create prover
      val prover: ErgoProver = ctx.newProverBuilder()
        .withMnemonic(
              SecretString.create(mnemonic),
              SecretString.empty(),
              false
        )
        .withEip3Secret(0)
        .build()

      // 1. creating issuer box transaction

      // get input box
      val myBoxId: String = boxId
      val address = Address.create(walletAddress)
      val inputBox = ctx.getDataSource.getBoxById(myBoxId, false, false)
      val minerFee: Long = Parameters.MinFee

      // To encode metadata we do everything in small steps
      // version
      val version = ErgoValue.of(2)

      // royalty
      val royalty = ErgoValue.of(Array[Byte]())// no royalty, but could be interesting use case for zengate tea???

      // properties
      val properties_entry1 = ErgoValue.pairOf(ErgoValue.of("teaType".getBytes(Charset.defaultCharset())), ErgoValue.of("chai".getBytes(Charset.defaultCharset())))
      val properties_entry1_type = properties_entry1.getType

      val properties_entry2 = ErgoValue.pairOf(ErgoValue.of("teaFlavor".getBytes(Charset.defaultCharset())), ErgoValue.of("mild".getBytes(Charset.defaultCharset())))

      val properties = ErgoValue.of(Array(properties_entry1.getValue, properties_entry2.getValue), properties_entry1_type)
      //val properties_type = properties.getType

      // levels
      val levels_entry1 = ErgoValue.pairOf(ErgoValue.of("caffeineContentInMilliGrams".getBytes(Charset.defaultCharset())), ErgoValue.pairOf(ErgoValue.of(80), ErgoValue.of(100)))
      val level_entry1_type = levels_entry1.getType

      val levels = ErgoValue.of(Array(levels_entry1.getValue), level_entry1_type)
      //val levels_type = levels.getType

      // stats
      val stats_entry1 = ErgoValue.pairOf(ErgoValue.of("pricePerKgInUSD".getBytes(Charset.defaultCharset())), ErgoValue.pairOf(ErgoValue.of(100), ErgoValue.of(1000)))
      val stats_entry1_type = stats_entry1.getType

      val stats = ErgoValue.of(Array(stats_entry1.getValue), stats_entry1_type)
      //val stats_type = stats.getType

      // metadata
      val metadata = ErgoValue.pairOf(properties, ErgoValue.pairOf(levels, stats))

      // collection token id
      val collectionTokenId = ErgoValue.of(Array[Byte]())

      // additional info
      val additionalInfo = ErgoValue.of(Array[Byte]())

      // create issuer box: please refer to EIP-24
      val issuerBoxValue: Long = 100*minerFee
      val issuerBoxAddress: Address = address
      val issuerBoxContract = issuerBoxAddress.toErgoContract
      val issuerBox: OutBox = issuerCreationTxBuilder.outBoxBuilder()
        .value(issuerBoxValue)
        .contract(issuerBoxContract)
        .registers(
          version,
          royalty,
          metadata,
          collectionTokenId,
          additionalInfo
        )
        .build()

      val unsignedIssuerCreationTx: UnsignedTransaction = issuerCreationTxBuilder.addInputs(inputBox)
        .addOutputs(issuerBox)
        .fee(minerFee)
        .sendChangeTo(address)
        .build()

      // note: we will sign both transactions at the same time to chain them, in practice a check should be put in place in case the first one fails, even if executed right after the other

      // 2. creating mint transaction

      // we must create a new tx builder for this second transaction, this is very important!!!
      val mintTxBuilder = ctx.newTxBuilder()

      // this will be the new issuer box input to the minting transaction
      val issuerBoxInput: InputBox = issuerBox.convertToInputWith(unsignedIssuerCreationTx.getId, 0) // the issuer box will be the first output box in the transaction

      // create token
      val tokenId: String = issuerBoxInput.getId.toString
      val tokenAmount: Long = 1 // neo: set to 1 for NFT, but can be any amount you want in principle, i.e. for "fungible" tokens
      val tokenDecimals: Int = 0
      val tokenContentHash = Sha256.hash(tokenContent)
      val myToken: Eip4Token = Eip4TokenBuilder.buildNftPictureToken(
        tokenId,
        tokenAmount,
        tokenName,
        tokenDescription,
        tokenDecimals,
        tokenContentHash,
        tokenLink
      )

      // create issuance box
      val issuanceBoxValue: Long = 10*minerFee
      val issuanceBoxAddress: Address = address
      val issuanceBoxContract: ErgoContract = issuanceBoxAddress.toErgoContract
      val issuanceBox: OutBox = mintTxBuilder.outBoxBuilder()
        .value(issuanceBoxValue)
        .mintToken(myToken)
        .contract(issuanceBoxContract)
        .build()

      // create unsigned mint transaction
      val unsignedMintForDummiesTx: UnsignedTransaction = mintTxBuilder.addInputs(issuerBoxInput)
        .addOutputs(issuanceBox)
        .fee(minerFee)
        .sendChangeTo(address)
        .build()

      // sign both the issuer creation tx and mint tx
      val signedIssuerCreationTx: SignedTransaction = prover.sign(unsignedIssuerCreationTx)
      val signedMintForDummiesTx: SignedTransaction = prover.sign(unsignedMintForDummiesTx)
      val issuerCreationTxId: String = ctx.sendTransaction(signedIssuerCreationTx)
      val mintForDummiesTxId: String = ctx.sendTransaction(signedMintForDummiesTx)

      (issuerCreationTxId, mintForDummiesTxId)

    })

    // remove quotation marks from string.
    (mintForDummiesTxIds._1.replaceAll("\"", ""), mintForDummiesTxIds._2.replaceAll("\"", ""))
  }


}