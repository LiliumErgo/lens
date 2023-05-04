package app

import org.ergoplatform.appkit.{ErgoClient, ErgoValue, InputBox, JavaHelpers, NetworkType}
import special.collection.Coll

import java.nio.charset.Charset
import scala.collection.JavaConverters._
import scala.reflect._


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
    val regValue = reg.getValue
    val classTag = reg.getType.getRType.classTag
    val decodedTokenName: String = processStringCollection(regValue)(classTag)
    println("Register 4 (token name): " + decodedTokenName)

  }

  private def printTokenDescription(): Unit = {

    val reg = registers(1)
    val regValue = reg.getValue
    val classTag = reg.getType.getRType.classTag
    val decodedDescriptionName: String = processStringCollection(regValue)(classTag)
    println("Register 5 (token description): " + decodedDescriptionName)

  }

  private def printNumberOfDecimals(): Unit = {

    val reg = registers(2)
    val regValue = reg.getValue
    val classTag = reg.getType.getRType.classTag
    val decodedDecimals: String = processStringCollection(regValue)(classTag)

    println("Register 6 (number of decimals): " + decodedDecimals)

  }

  private def printAssetType(): Unit = {

    val reg = registers(3)
    val regValue = reg.getValue
    val runtime = reg.getType.getRType.classTag.runtimeClass
    val decodedAssetType: String = (regValue, runtime) match {
      case (assetType: Coll[_], t) if t == classOf[Coll[Byte]] =>
        val assetTypeTyped = assetType.asInstanceOf[Coll[Byte]]
        val assetTypeByteArray: Array[Byte] = JavaHelpers.collToByteArray(assetTypeTyped)
        assetTypeByteArray.mkString("Coll(", ", ", ")")
    }

    println("Register 7 (asset type): " + decodedAssetType)

  }

  protected def printRegister8(): Unit
  protected def printRegister9(): Unit

  protected def processStringCollection(coll: Any)(implicit tag: ClassTag[_]): String = {
    (coll, tag.runtimeClass) match {
      case (string: Coll[_], t) if t == classOf[Coll[Byte]] =>
        val stringTyped = string.asInstanceOf[Coll[Byte]]
        val stringByteArray: Array[Byte] = JavaHelpers.collToByteArray(stringTyped)
        new String(stringByteArray, Charset.defaultCharset())
    }
  }

  protected def processHash(coll: Any)(implicit tag: ClassTag[_]): String = {
    (coll, tag.runtimeClass) match {

      case (hash: Coll[_], t) if t == classOf[Coll[Byte]] =>

        val hashTyped = hash.asInstanceOf[Coll[Byte]]
        val hashByteArray: Array[Byte] = JavaHelpers.collToByteArray(hashTyped)
        val hashString: String = hashByteArray.map("%02x".format(_)).mkString
        hashString

      case (hashColl: Coll[_], t) if t == classOf[Coll[Coll[Byte]]] =>

        val hashCollTyped = hashColl.asInstanceOf[Coll[Coll[Byte]]]
        val hashStringColl: Coll[String] = hashCollTyped.map(h => processHash(h)(ClassTag(h.getClass)))
        hashStringColl.toString()
    }

  }

}
