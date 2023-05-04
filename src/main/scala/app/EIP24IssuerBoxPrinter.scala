package app

import org.ergoplatform.appkit.{ErgoClient, ErgoValue, InputBox, JavaHelpers, NetworkType}
import sigmastate.Values.ErgoTree
import sigmastate.serialization.ErgoTreeSerializer
import special.collection.Coll

import java.nio.charset.Charset
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

case class EIP24IssuerBoxPrinter(ergoClient: ErgoClient, networkType: NetworkType, issuerBoxId: String) extends ErgoBoxPrinter {

  override val boxId: String = issuerBoxId
  override val box: InputBox = ergoClient.execute { ctx =>
    ctx.getDataSource.getBoxById(boxId, true, true)
  }
  override val registers: List[ErgoValue[_]] = box.getRegisters.asScala.toList

  override def printBox(): Unit = {

    println("Printing registers for eip24 issuer box id: " + boxId)

    printRawRegisters()
    printDecodedRegisters()

  }

  override def printDecodedRegisters(): Unit = {

    println("Printing decoded register data")

    printReservedRegisters()

    printVersion()
    printRoyaltyInfo()
    printTraits()
    printCollectionTokenId()
    printAdditionalInformation()

  }

  private def printVersion(): Unit = {

    val reg = registers(0)
    val regValue = reg.getValue
    val decodedVersion: String = regValue match {

      case version: Integer => if (version >= 100) "1" else version.toString
      case _ => "1"

    }

    println("Register 4 (version): " + decodedVersion)

  }

  private def printRoyaltyInfo(): Unit = {

    val reg = registers(1)
    val regValue = reg.getValue
    val decodedRoyalty = processCollection(regValue)(ClassTag(regValue.getClass))

    println("Register 5 (royalty recipients): " + decodedRoyalty)

  }

  private def printTraits(): Unit = {

    val reg = registers(2)
    val regValue = reg.getValue
    val decodedTraits = processCollection(regValue)(ClassTag(regValue.getClass))

    println("Register 6 (traits): " + decodedTraits)

  }

  private def printCollectionTokenId(): Unit = {

    val reg = registers(3)
    val regValue = reg.getValue
    val decodedCollectionTokenId = processCollection(regValue)(ClassTag(regValue.getClass))

    println("Register 7 (collection token id): " + decodedCollectionTokenId)

  }

  private def printAdditionalInformation(): Unit = {

    val reg = registers(4)
    val regValue = reg.getValue
    val decodedAdditionalInfo = processCollection(regValue)(ClassTag(regValue.getClass))

    println("Register 8 (additional information): " + decodedAdditionalInfo)

  }

}
