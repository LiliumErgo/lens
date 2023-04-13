package app


import org.ergoplatform.appkit.{ErgoClient, NetworkType, RestApiErgoClient}
import protocol.MintForDummiesUtils

/**
  * Main object of the GuapSwap CLI application.
  */
object MintForDummies extends App {

    // Setup Ergo Client
    val boxId: String = args(0)
    val walletAddress: String = args(1)
    val mnemonic: String = args(2)
    val apiUrl: String = "http://213.239.193.208:9053/"
    val networkType: NetworkType = NetworkType.MAINNET
    val explorerURL: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
    val ergoClient: ErgoClient = RestApiErgoClient.create(apiUrl, networkType, "", explorerURL)

    println(Console.GREEN + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} ERGO CLIENT CREATED SUCCESSFULLY ==========" + Console.RESET)

    println(Console.YELLOW + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} MINTFORDUMMIES TX INITIATED ==========" + Console.RESET)

    val mintForDummiesTxId: String = MintForDummiesCommands.mint(ergoClient, boxId, walletAddress, mnemonic)

    println(Console.GREEN + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} GUAPSWAP ONETIME TX SUCCESSFULL ==========" + Console.RESET)

    // Print tx link to the user
    println(Console.BLUE + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} VIEW MINTFORDUMMIES TX IN THE ERGO-EXPLORER WITH THE LINK BELOW ==========" + Console.RESET)
    println(MintForDummiesUtils.ERGO_EXPLORER_TX_URL_PREFIX + mintForDummiesTxId)

}