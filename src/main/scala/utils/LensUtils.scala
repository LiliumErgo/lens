package utils

import configs.collection_config.LensCollectionConfig
import configs.nft_config.LensNFTConfig

import org.guapswap._

import org.ergoplatform.appkit.impl.Eip4TokenBuilder
import org.ergoplatform.appkit.{Address, BlockchainContext, ErgoToken, ErgoTreeTemplate, InputBox, OutBox, UnsignedTransaction, UnsignedTransactionBuilder}
import scorex.util.encode.Base16

import java.time.LocalDateTime
import java.time.ZoneId
import sttp.client4._
import sttp.client4.circe.asJson
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object LensUtils {

  final val ERGO_EXPLORER_TX_URL_PREFIX_MAINNET: String = "https://explorer.ergoplatform.com/en/transactions/"
  final val ERGO_EXPLORER_TX_URL_PREFIX_TESTNET: String = "https://testnet.ergoplatform.com/en/transactions/"

  final val LILIUM_STATE_BOX_ERGOTREE_TEMPLATE_HEX: String = "d812d601e4c6a70759d602db6807b2db6502fe730000d603908c7201017202d604e4c6a7080dd605b27204730100d606b27204730200d6078c720102d6087303d6097304d60ae4c6a7091ad60bb2720a730500d60cb27204730600d60d7307d60ed9010e0195ec720e93e4c6b2a473080005057309730a938cb2db6308b2a5730b00730c00018cb2db6308a7730d0001d60fb2720a730e00d610b27204730f00d611b27204731000d612b2720a73110095ecec72037205720695ec91720772029372077312d80fd613e4e30163d614e4c6a70605d6159a72147313d616e4c672130905d617b2a4731400d618b2a5731500d619e4c67218063c0c3c0e0e3c0c3c0e580c3c0e58d61a8c721902d61b7316d61ce4c6a70564d61d9372157216d61eb2a5731700d61f95ef721dd805d61fdb6308721ed620b2721f731800d621db6308a7d622b2721f731900d623b27221731a009683040193722086028cb27221731b0001731c938c722001720993722286028c722301998c722302731d938c7222017208d802d61fdb6308721ed620b2721f731e009683030193722086028cb2db6308a7731f00017320938c722001720993b1721f7321d620b2a5732200d621b2a5732300d19683070193c572137208907215721693c2721773249683080193c17218732593c27218732693b2db6308721873270086027208732893e4c672180404732993cbb0e4c67218050c4c0e7a732ad901223c0e4c0ed801d6248c722202b3b38c7222018c7224017a7e8c72240205732b93cbb08c721a02b3b08c721a01b3b08c72190195937eb28cb2e4c67218080c3c0e0e732c0002732d0004732e7a732f7a7330d901223c0e3c0e0ed803d6248c722202d6258c722401d6268c722402b3b3b3b38c7222017a7eb172250572257a7eb17226057226721bd901223c0e3c0e58d803d6248c722202d6258c722401d6268c722402b3b3b3b38c7222017a7eb172250572257a7e8c722601057a7e8c72260205721bd901223c0e3c0e58d803d6248c722202d6258c722401d6268c722402b3b3b3b38c7222017a7eb172250572257a7e8c722601057a7e8c72260205cbe4dc640a721c02cb7a7214e4e3000e93e4c67218070e720893e4c672180944058602e4c672170408721495721d9683060193c1721ec1a793c2721ec2a7721f93db6401e4c6721e0464db6401e4c6a7046493db6401e4c6721e0564db6401721c93e4c6721e060572159683090193c1721ec1a793c2721ec2a7721f93db6401e4c6721e0464db6401e4c6a7046493db6401e4c6721e0564db6401721c93e4c6721e0605721593e4c6721e0759720193e4c6721e080d720493e4c6721e091a720a95720395ed720caedb63087217d901224d0e9372228602720b7331d802d622c17217d6238f72227332968304019683030193c17220733393b2db630872207334008602720b733593c27220d0720dda720e01927222733693c195ed722391e4c6b2a473370005057338b2a5733900b2a5733a00733b92c195ed722391e4c6b2a4733c000505733db2a5733e00b2a5733f00734095ed7210aedb63087217d901224d0eed938c722201720f928c7222027341d802d622c17217d6238f72227342968304019683030193c17220734393c27220d0720d93b2db630872207344008602720f7345da720e01927222734693c195ed722391e4c6b2a473470005057348b2a5734900b2a5734a00734b92c195ed722391e4c6b2a4734c000505734db2a5734e00b2a5734f007350957211d802d622c17217d6238f72227351968304019683020193c17220735293c27220d0720dda720e01927222735393c195ed722391e4c6b2a473540005057355b2a5735600b2a5735700735892c195ed722391e4c6b2a47359000505735ab2a5735b00b2a5735c00735d735e95ec7205ed7206720c95ed7206aedb63087217d901224d0e9372228602720b735fd802d622c17217d6238f72227360968304019683030193c17220736193b2db630872207362008602720b736393c27220d0720dda720e01927222736493c195ed722391e4c6b2a473650005057366b2a5736700b2a5736800736992c195ed722391e4c6b2a4736a000505736bb2a5736c00b2a5736d00736e95ed7205aedb63087217d901224d0e93722286027212736f95ed7210aedb63087217d901224d0eed938c722201720f928c7222027370d803d622db63087220d623c17217d6248f72237371968304019683030193b2722273720086027212737393b272227374008602720f737593c27220d0720dda720e01927223737693c195ed722491e4c6b2a473770005057378b2a5737900b2a5737a00737b92c195ed722491e4c6b2a4737c000505737db2a5737e00b2a5737f00738001957211d802d622c17217d6238f7222738101968304019683030193c1722073820193b2db63087220738301008602721273840193c27220d0720dda720e0192722273850193c195ed722391e4c6b2a4738601000505738701b2a573880100b2a573890100738a0192c195ed722391e4c6b2a4738b01000505738c01b2a5738d0100b2a5738e0100738f017390017391017392019683020193c1722173930193c27221d0739401d801d613b2a573950100d1968303019683030193c172139999c1a773960173970193c27213d0720d95b2720473980100d802d614b2db6308721373990100d615b2db6308a7739a0100968303019372147215938c721401720893b0ada5d9011663b0addb63087216d901184d0e8c721802739b01d90118599a8c7218018c721802739c01d90116599a8c7216018c7216028c721502afa5d901146393b1db63087214739d0193c1b2a5739e0100739f0192c1b2a573a0010073a101d173a201"
  final val LILIUM_COLLECTION_ISSUANCE_ERGOTREE_TEMPLATE_HEX: String = "d803d601b2a5730000d602e4e30163d603c57202ea02d1ed93c27201e4e3000eed93b2db6308720173010086027203e4c672020905938cb2db6308a77302000172037303"

  final val SKYHARBOR_SALE_BOX_ERGOTREE_TEMPLATE_HEX: String = "95ed91b1a5730094cbc2b2a47301007302d804d601e4c6a70663d602c672010404d603e4c6a70405d6049d72037303d195e67202d804d605b2a5730400d606e47202d6079d9c72037e7206057305d608b2a57306009683070192c17205999972037204720793c27205e4c6a7050e93e4c67205040ec5a792c17208720493c27208730795eded947207730891720673098f7206730ad801d609b2a5730b00ed92c17209720793c27209c27201730c93c572018cb2db6308a7730d0001d802d605b2a5730e00d606b2a5730f009683060192c17205997203720493c27205e4c6a7050e93e4c67205040ec5a792c17206720493c27206731093c572018cb2db6308a773110001cde4c6a70707"

  final val LILIUM_MAINNET_FEE_ADDRESS: String = ""
  final val LILIUM_TESTNET_FEE_ADDRESS: String = ""

  final val LILIUM_COLLECTION_CONFIG_PATH: String = "./data/collection_metadata.json"
  final val LILIUM_NFT_METADATA_CONFIG_PATH: String = "./data/nft_metadata.json"

  /**
    * Get a time-zone timestamp.
    *
    * @param zone The desired time zone.
    * @return A time-zone timestamp, with date and time.
    */
  def getTimeStamp(zone: String): String = {

    // Get the date and time in UTC format
    val dateTime: LocalDateTime = LocalDateTime.now(ZoneId.of(zone))

    // Format the time string 
    val date: String = dateTime.toString.split("[T]")(0)
    val time: String = dateTime.toString.split("[T]")(1).split("\\.")(0)
    val timestamp: String = s"[$zone $date $time]"

    timestamp
  }

  def getErgoTreeTemplateHex(ergotree: String): String = {
    val bytes = Base16.decode(ergotree).get
    val templatebytes = ErgoTreeTemplate.fromErgoTreeBytes(bytes)
    val encoded = templatebytes.getEncodedBytes
    encoded
  }

  def liliumFeeInUSD(collectionSize: Long): Double = {

    val alpha: Double = 54.10
    val beta: Double = 0.03

    val phi: Double = math.floor(alpha * math.log(beta * collectionSize + 1))

    phi

  }

  def liliumFeeInNanoERG(collectionSize: Long): Long = {


    case class Ergo(usd: Double)
    case class ErgoPrice(ergo: Ergo)
    implicit val jsonDecoder: Decoder[ErgoPrice] = deriveDecoder[ErgoPrice]

    val usdFee: Double = liliumFeeInUSD(collectionSize)

    val backend = HttpURLConnectionBackend()

    val request = basicRequest.get(uri"https://api.coingecko.com/api/v3/simple/price?ids=ergo&vs_currencies=usd%22").response(asJson[ErgoPrice])

    val response = request.send(backend)

    try {

      val usdPerErg: Double = response.body.right.get.ergo.usd

      val ergs = usdFee / usdPerErg

      val nanoErgs = (ergs * 1000000000).toLong

      nanoErgs

    } catch {
      case e: Throwable => throw e
    }

  }

  def getNFTIssuer(collectionConfig: LensCollectionConfig, nftConfig: LensNFTConfig, collectionIssuance: InputBox, minerFee: Long, address: Address, index: Int)(implicit txBuilder: UnsignedTransactionBuilder): OutBox = {

    val collectionId = collectionIssuance.getTokens.get(0).getId
    val collectionToken = new ErgoToken(collectionId, 1)

    val nftIssuer = txBuilder.outBoxBuilder()
      .value(10000000)
      .contract(address.toErgoContract)
      .tokens(collectionToken)
      .registers(
        sigmabuilders.encoders.minting_encoders.EIP24IssuerEncoder.encodeR4(nftConfig.getArtworkStandardVersion),
        sigmabuilders.encoders.minting_encoders.EIP24IssuerEncoder.encodeR5(collectionConfig.getCollectionRoyalties),
        sigmabuilders.encoders.minting_encoders.EIP24IssuerEncoder.encodeR6(
          nftConfig.getProperties,
          nftConfig.getLevels,
          nftConfig.getStats
        ),
        sigmabuilders.encoders.minting_encoders.EIP24IssuerEncoder.encodeR7(collectionId),
        sigmabuilders.encoders.minting_encoders.EIP24IssuerEncoder.encodeR8(nftConfig.getAdditionalInfo)
      )
      .build()

    nftIssuer

  }

  def mintNFT(nftConfig: LensNFTConfig, nftIssuer: InputBox, minerFee: Long, address: Address)(implicit ctx: BlockchainContext): UnsignedTransaction = {

    val mintTxBuilder = ctx.newTxBuilder()

    val nftIssuance = mintTxBuilder.outBoxBuilder()
      .value(nftIssuer.getValue - minerFee)
      .contract(address.toErgoContract)
      .mintToken(
        Eip4TokenBuilder.buildNftPictureToken(
          nftIssuer.getId.toString,
          1,
          nftConfig.getNFTName,
          nftConfig.getNFTDescription,
          0,
          nftConfig.getImageHash.getBytes("utf-8"),
          nftConfig.getImageLink

        )
      )
      .build()

    val mintTx = mintTxBuilder
      .addInputs(nftIssuer)
      .addOutputs(nftIssuance)
      .fee(minerFee)
      .sendChangeTo(address)
      .build()

    mintTx

  }

}