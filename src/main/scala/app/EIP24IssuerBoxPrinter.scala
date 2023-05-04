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

  private def processCollection(coll: Any)(implicit tag: ClassTag[_]): String = {

    (coll, tag.runtimeClass) match {

      case (royalties: Coll[_], t) if t == classOf[Coll[(Coll[Byte], Integer)]] =>

        val royaltiesTyped = royalties.asInstanceOf[Coll[(Coll[Byte], Integer)]]
        val stringColl: Coll[(String, String)] = royaltiesTyped.map(r => {
          val address: ErgoTree = ErgoTreeSerializer.DefaultSerializer.deserializeErgoTree(r._1.toArray)
          val share: Integer = r._2
          (address.bytesHex, share.toString)
        })

        stringColl.toString()

      case (traits: (_, _), t) if t == classOf[(Coll[(Coll[Byte], Coll[Byte])], (Coll[(Coll[Byte], (Integer, Integer))], Coll[(Coll[Byte], (Integer, Integer))]))] =>

        val traitsTyped = traits.asInstanceOf[(Coll[(Coll[Byte], Coll[Byte])], (Coll[(Coll[Byte], (Integer, Integer))], Coll[(Coll[Byte], (Integer, Integer))]))]
        val properties: Coll[(Coll[Byte], Coll[Byte])] = traitsTyped._1
        val levels: Coll[(Coll[Byte], (Integer, Integer))] = traitsTyped._2._1
        val stats: Coll[(Coll[Byte], (Integer, Integer))] = traitsTyped._2._2

        val propertiesStringColl: Coll[(String, String)] = properties.map(p => {

          val keyByteArray: Array[Byte] = JavaHelpers.collToByteArray(p._1)
          val valueByteArray: Array[Byte] = JavaHelpers.collToByteArray(p._2)
          val key: String = new String(keyByteArray, Charset.defaultCharset())
          val value: String = new String(valueByteArray, Charset.defaultCharset())

          (key, value)

        })

        val levelsStringColl: Coll[(String, String)] = stats.map(l => {

          val keyByteArray: Array[Byte] = JavaHelpers.collToByteArray(l._1)
          val key: String = new String(keyByteArray, Charset.defaultCharset())
          val valueMax: String = l._2.toString()

          (key, valueMax)

        })

        val statsStringColl: Coll[(String, String)] = levels.map(s => {

          val keyByteArray: Array[Byte] = JavaHelpers.collToByteArray(s._1)
          val key: String = new String(keyByteArray, Charset.defaultCharset())
          val valueMax: String = s._2.toString()

          (key, valueMax)

        })

        "{ properties: " + propertiesStringColl.toString() + ", levels: " + levelsStringColl.toString() + ", stats: " + statsStringColl.toString() + "}"

      case (info: Coll[_], t) if t == classOf[Coll[(Coll[Byte], Coll[Byte])]] =>

        val infoTyped = info.asInstanceOf[Coll[(Coll[Byte], Coll[Byte])]]
        val infoStringColl: Coll[(String, String)] = infoTyped.map(i => {

          val keyByteArray: Array[Byte] = JavaHelpers.collToByteArray(i._1)
          val key: String = new String(keyByteArray, Charset.defaultCharset())

          val valueByteArray: Array[Byte] = JavaHelpers.collToByteArray(i._2)
          val value: String = new String(valueByteArray, Charset.defaultCharset())

          (key, value)

        })

        infoStringColl.toString()

      case _ => ""

    }

  }

}
