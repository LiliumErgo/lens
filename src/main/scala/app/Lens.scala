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

        val mintType: String = args(2)
        val signerMnemonic: String = args(3)
        val signerAddress: String = args(4)
        val recipientAddress: String = args(5)

        if (mintType.equals("single")) {

            println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS SINGLE MINT TX INITIATED ==========" + Console.RESET)

            //liliumTxIds = LensCommands.mint(ergoClient, networkTypeString, mnemonic, walletAddress)

            println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} LENS SINGLE MINT TX SUCCESSFUL ==========" + Console.RESET)

        } else if (mintType.equals("collection")) {

            println(Console.YELLOW + s"========== ${LensUtils.getTimeStamp("UTC")} LENS COLLECTION MINT TX INITIATED ==========" + Console.RESET)

            LensCommands.mintCollection(ergoClient, networkTypeString, signerMnemonic, signerAddress, recipientAddress)

            println(Console.GREEN + s"========== ${LensUtils.getTimeStamp("UTC")} LENS COLLECTION MINT TX SUCCESSFUL ==========" + Console.RESET)

        } else {
            throw new IllegalArgumentException("invalid mint type variable")
        }

        // Print tx links to the user
        println(Console.BLUE + s"========== ${LensUtils.getTimeStamp("UTC")} VIEW LENS MINT TXS IN THE ERGO-EXPLORER WITH THE LINKS BELOW ==========" + Console.RESET)

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