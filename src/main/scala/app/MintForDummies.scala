package app


import org.ergoplatform.appkit.{ErgoClient, NetworkType, RestApiErgoClient}
import protocol.MintForDummiesUtils

import scala.sys.exit

object MintForDummies extends App {

    // Setup Ergo Client
    val operation: String = args(0)
    val networkTypeString: String = args(1).toLowerCase()
    val apiUrl: String = if (networkTypeString.equals("mainnet")) "http://213.239.193.208:9053/" else "http://168.138.185.215:9052/"
    val networkType: NetworkType = if (networkTypeString.equals("mainnet")) NetworkType.MAINNET else NetworkType.TESTNET
    val explorerURL: String = RestApiErgoClient.getDefaultExplorerUrl(networkType)
    val ergoClient: ErgoClient = RestApiErgoClient.create(apiUrl, networkType, "", explorerURL)

    println(Console.GREEN + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} ERGO CLIENT CREATED SUCCESSFULLY ==========" + Console.RESET)

    if (operation.equals("--print")) {

        val boxType: String = args(2)
        val boxId: String = args(3)

        println(Console.YELLOW + s"========== ${MintForDummiesUtils.getTimeStamp("UTC")} MINTFORDUMMIES REGISTER PRINTER INITIATED ==========" + Console.RESET)
        MintForDummiesCommands.print(ergoClient, networkType, boxType, boxId)
        exit(0)

    } else if (operation.equals("--mint")) {

        val tokenName: String = args(2)
        val tokenDescription: String = args(3)
        val tokenContent: String = args(4)
        val tokenLink: String = args(5)
        val boxId: String = args(6)
        val walletAddress: String = args(7)
        val mnemonic: String = args(8)

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
        exit(0)

    } else {
        throw new IllegalArgumentException("invalid operation type")
    }


}