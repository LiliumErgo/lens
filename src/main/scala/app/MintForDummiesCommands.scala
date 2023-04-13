package app

import org.ergoplatform.appkit._
import org.ergoplatform.appkit.impl.Eip4TokenBuilder
import scorex.crypto.hash.Sha256

import java.util

object MintForDummiesCommands {

    /**
      * Perform a onetime swap.
      *
      * @param ergoClient
      * @param nodeConfig
      * @param parameters
      * @param proxyAddress
      * @param unlockedSecretStorage
      * @return Onetime GuapSwap transaction ID string.
      */
    def mint(ergoClient: ErgoClient, boxId: String, walletAddress: String, mnemonic: String): String = {

        // Generate blockchain context
        val mintForDummiesTxId: String = ergoClient.execute((ctx: BlockchainContext) => {

            // get input box
            val myBoxId: String = boxId
            val address = Address.create(walletAddress)
            val inputBox = ctx.getDataSource.getBoxById(myBoxId, false, false)
            val minerFee: Long = Parameters.MinFee
            val mintBoxValue: Long = 100 * minerFee

            // create tx builder
            val txBuilder: UnsignedTransactionBuilder = ctx.newTxBuilder()

            // create token
            val tokenId: String = myBoxId
            val tokenAmount: Long = 666
            val tokenName: String = "My Dumb Token"
            val tokenDescription: String = "Best token ever, believe me."
            val tokenDecimals: Int = 0
            val tokenMetadata: String = "My token metadata"
            val tokenMetadataHash = Sha256.hash(tokenMetadata)
            val tokenLink: String = "empty.space"
            val myToken: Eip4Token = Eip4TokenBuilder.buildNftPictureToken(
                tokenId,
                tokenAmount,
                tokenName,
                tokenDescription,
                tokenDecimals,
                tokenMetadataHash,
                tokenLink
            )

            // Create output mintBox
            val mintAddress: Address = address
            val mintContract: ErgoContract = mintAddress.toErgoContract
            val mintBox: OutBox = txBuilder.outBoxBuilder()
              .value(mintBoxValue)
              .mintToken(myToken)
              .contract(mintContract)
              .build()

            // Create prover
            val prover: ErgoProver = ctx.newProverBuilder()
              .withMnemonic(
                    SecretString.create(mnemonic),
                    SecretString.empty(),
                    false
              )
              .withEip3Secret(0)
              .build()

            // Create unsigned transaction
            val unsignedMintForDummiesTx: UnsignedTransaction = txBuilder.addInputs(inputBox)
              .addOutputs(mintBox)
              .fee(minerFee)
              .sendChangeTo(mintAddress)
              .build()

            // Sign transaction
            val signedMintForDummiesTx: SignedTransaction = prover.sign(unsignedMintForDummiesTx)
            val mintForDummiesTxId: String = ctx.sendTransaction(signedMintForDummiesTx)
            mintForDummiesTxId

        })

        // Remove quotation marks from string.
        mintForDummiesTxId.replaceAll("\"", "")
    }

}