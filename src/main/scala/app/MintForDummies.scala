package app


import org.ergoplatform.appkit.{ErgoClient, NetworkType, RestApiErgoClient}
import protocol.MintForDummiesUtils

object MintForDummies extends App {

    // Setup Ergo Client
    val networkTypeString: String = args(0).toLowerCase()
    val tokenName: String = args(1)
    val tokenDescription: String = args(2)
    val tokenContent: String = args(3)
    val tokenLink: String = args(4)
    val boxId: String = args(5)
    val walletAddress: String = args(6)
    val mnemonic: String = args(7)
    val apiUrl: String = if (networkTypeString.equals("mainnet")) "http://213.239.193.208:9053/" else "http://168.138.185.215:9052/"
    val networkType: NetworkType = if (networkTypeString.equals("mainnet")) NetworkType.MAINNET else NetworkType.TESTNET
    val explorerURL: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
    val ergoClient: ErgoClient = RestApiErgoClient.create(apiUrl, networkType, "", explorerURL)

    println(Console.GREEN + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} ERGO CLIENT CREATED SUCCESSFULLY ==========" + Console.RESET)

    println(Console.YELLOW + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} MINTFORDUMMIES TX INITIATED ==========" + Console.RESET)

    val mintForDummiesTxIds: (String, String) = MintForDummiesCommands.mint(ergoClient, tokenName, tokenDescription, tokenContent, tokenLink, boxId, walletAddress, mnemonic)

    println(Console.GREEN + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} MINTFORDUMMIES TX SUCCESSFULL ==========" + Console.RESET)

    // Print tx link to the user
    println(Console.BLUE + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} VIEW MINTFORDUMMIES: ISSUER CREATION AND MINT TXS IN THE ERGO-EXPLORER WITH THE LINKS BELOW ==========" + Console.RESET)
    if (networkTypeString.equals("mainnet")) {
        println(MintForDummiesUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._1)
        println(MintForDummiesUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._2)
    } else {
        println(MintForDummiesUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._1)
        println(MintForDummiesUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._2)

    }


}