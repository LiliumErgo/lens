package app


import org.ergoplatform.appkit.{ErgoClient, NetworkType, RestApiErgoClient}
import utils.LensUtils

import scala.sys.exit

object Lens extends App {

    // Setup Ergo Client
    val operation: String = args(0)
    val networkTypeString: String = args(1).toLowerCase()
    val nodeUrl: String = if (networkTypeString.equals("mainnet")) "https://ergo-mainnet-node.guapswap.org" else "https://ergo-testnet-node.guapswap.org"
    val networkType: NetworkType = if (networkTypeString.equals("mainnet")) NetworkType.MAINNET else NetworkType.TESTNET
    val explorerUrl: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
    val ergoClient: ErgoClient = RestApiErgoClient.create(nodeUrl, networkType, "", explorerUrl)

    println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} ERGO CLIENT CREATED SUCCESSFULLY ==========" + Console.RESET)

    if (operation.equals("--print")) {

        val boxType: String = args(2)
        val boxId: String = args(3)

        println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS REGISTER PRINTER INITIATED ==========" + Console.RESET)
        LensCommands.print(ergoClient, networkType, boxType, boxId)
        println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} LENS REGISTER PRINTER SUCCESSFUL ==========" + Console.RESET)
        exit(0)

    } else if (operation.equals("--mint")) {

        val tokenName: String = args(2)
        val tokenDescription: String = args(3)
        val tokenContent: String = args(4)
        val tokenLink: String = args(5)
        val boxId: String = args(6)
        val walletAddress: String = args(7)
        val mnemonic: String = args(8)

        println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS TX INITIATED ==========" + Console.RESET)

        val mintForDummiesTxIds: (String, String) = LensCommands.mint(ergoClient, tokenName, tokenDescription, tokenContent, tokenLink, boxId, walletAddress, mnemonic)

        println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} LENS TX SUCCESSFUL ==========" + Console.RESET)

        // Print tx link to the user
        println(Console.BLUE + s"========== ${LensUtils.getTimeStamp("UTC")} VIEW LENS ISSUER CREATION AND MINT TXS IN THE ERGO-EXPLORER WITH THE LINKS BELOW ==========" + Console.RESET)
        if (networkTypeString.equals("mainnet")) {
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._1)
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_MAINNET + mintForDummiesTxIds._2)
        } else {
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._1)
            println(LensUtils.ERGO_EXPLORER_TX_URL_PREFIX_TESTNET + mintForDummiesTxIds._2)
        }
        exit(0)

    } else if (operation.equals("--snapshot")) {

        val collectionId: String = args(2)

        println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS SNAPSHOT INITIATED ==========" + Console.RESET)
        LensCommands.snapshot(networkType, nodeUrl, explorerUrl, collectionId)
        println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} LENS SNAPSHOT SUCCESSFUL ==========" + Console.RESET)
        exit(0)

    } else {
        throw new IllegalArgumentException("invalid operation type")
    }


}