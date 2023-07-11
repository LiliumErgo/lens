package app


import org.ergoplatform.appkit.{ErgoClient, NetworkType, RestApiErgoClient}
import utils.LensUtils

import scala.sys.exit

object Lens extends App {

    // Setup Ergo Client
    val operation: String = args(0)
    val networkTypeString: String = args(1).toLowerCase()
    val apiUrl: String = if (networkTypeString.equals("mainnet")) "http://ergo-mainnet-node.guapswap.org" else "http://ergo-testnet-node.guapswap.org"
    val networkType: NetworkType = if (networkTypeString.equals("mainnet")) NetworkType.MAINNET else NetworkType.TESTNET
    val explorerURL: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
    val ergoClient: ErgoClient = RestApiErgoClient.create(apiUrl, networkType, "", explorerURL)

    println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} ERGO CLIENT CREATED SUCCESSFULLY ==========" + Console.RESET)

    if (operation.equals("--print")) {

        val boxType: String = args(2)
        val boxId: String = args(3)

        println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS REGISTER PRINTER INITIATED ==========" + Console.RESET)
        LensCommands.print(ergoClient, networkType, boxType, boxId)
        exit(0)

    } else if (operation.equals("--mint")) {

        val tokenName: String = args(2)
        val tokenDescription: String = args(3)
        val tokenContent: String = args(4)
        val tokenLink: String = args(5)
        val boxId: String = args(6)
        val walletAddress: String = args(7)
        val mnemonic: String = args(8)

        println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} MINTFORDUMMIES TX INITIATED ==========" + Console.RESET)

        val mintForDummiesTxIds: (String, String) = LensCommands.mint(ergoClient, tokenName, tokenDescription, tokenContent, tokenLink, boxId, walletAddress, mnemonic)

        println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} MINTFORDUMMIES TX SUCCESSFULL ==========" + Console.RESET)

        // Print tx link to the user
        println(Console.BLUE + s"========== ${LensUtils.getTimeStamp("UTC")} VIEW MINTFORDUMMIES: ISSUER CREATION AND MINT TXS IN THE ERGO-EXPLORER WITH THE LINKS BELOW ==========" + Console.RESET)
        if (networkTypeString.equals("mainnet")) {
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._1)
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._2)
        } else {
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._1)
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._2)
        }
        exit(0)

    } else {
        throw new IllegalArgumentException("invalid operation type")
    }


}