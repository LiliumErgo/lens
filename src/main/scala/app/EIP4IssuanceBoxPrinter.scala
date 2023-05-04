package app

import org.ergoplatform.appkit.{ErgoClient, ErgoValue, InputBox, JavaHelpers, NetworkType}
import special.collection.Coll

import java.nio.charset.Charset
import scala.collection.JavaConverters._


abstract class EIP4IssuanceBoxPrinter(ergoClient: ErgoClient, networkType: NetworkType, issuanceBoxId: String) extends ErgoBoxPrinter {

  override val boxId: String = issuanceBoxId
  override val box: InputBox = ergoClient.execute { ctx =>
    ctx.getDataSource.getBoxById(boxId, true, true)
  }
  override val registers: List[ErgoValue[_]] = box.getRegisters.asScala.toList

  override def printBox(): Unit = {

    println("Printing registers for eip4 issuance box id: " + boxId)

    printRawRegisters()
    printDecodedRegisters()

  }

  override def printDecodedRegisters(): Unit = {

    println("Printing decoded register data")

    printReservedRegisters()

    printTokenVerboseName()
    printTokenDescription()
    printNumberOfDecimals()
    printAssetType()
    printRegister8()
    printRegister9()

  }

  private def printTokenVerboseName(): Unit = {

    val reg: ErgoValue[_] = registers(0)
    val decodedTokenName: String = reg.getValue match {
      case name: Coll[Byte] =>
        val nameByteArray: Array[Byte] = JavaHelpers.collToByteArray(name)
        val nameString: String = new String(nameByteArray, Charset.defaultCharset())
        nameString
    }

    println("Register 4 (token name): " + decodedTokenName)

  }

  private def printTokenDescription(): Unit = {

    val reg: ErgoValue[_] = registers(1)
    val decodedDescrptionName: String = reg.getValue match {
      case description: Coll[Byte] =>
        val descriptionByteArray: Array[Byte] = JavaHelpers.collToByteArray(description)
        val descriptionString: String = new String(descriptionByteArray, Charset.defaultCharset())
        descriptionString
    }

    println("Register 5 (token description): " + decodedDescrptionName)

  }

  private def printNumberOfDecimals(): Unit = {

    val reg: ErgoValue[_] = registers(2)
    val decodedDecimals: String = reg.getValue match {
      case decimals: Coll[Byte] =>
        val decimalsByteArray: Array[Byte] = JavaHelpers.collToByteArray(decimals)
        val decimalsString: String = new String(decimalsByteArray, Charset.defaultCharset())
        decimalsString
    }

    println("Register 6 (number of decimals): " + decodedDecimals)

  }

 private def printAssetType(): Unit = {

    val reg = registers(3)
    val decodedAssetType: String = reg.getValue match {
      case assetType: Coll[Byte] =>
        val assetTypeByteArray: Array[Byte] = JavaHelpers.collToByteArray(assetType)
        assetTypeByteArray.mkString("Coll(", ", ", ")")
    }

    println("Register 7 (asset type): " + decodedAssetType)

  }

  protected def printRegister8(): Unit
  protected def printRegister9(): Unit

}
