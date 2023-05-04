package app

import org.ergoplatform.appkit.{ErgoValue, InputBox, JavaHelpers}
import sigmastate.Values.ErgoTree
import sigmastate.serialization.ErgoTreeSerializer
import special.collection.Coll

import java.nio.charset.Charset
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

trait ErgoBoxPrinter {

  val boxId: String
  val box: InputBox
  val registers: List[ErgoValue[_]] = List()

  def printBox(): Unit

  def printReservedRegisters(): Unit = {

    println("Register 0 (value): " + box.getValue)
    println("Register 1 (ergotree): " + box.getErgoTree.bytesHex)
    println("Register 2 (tokens): " + box.getTokens.asScala.toList.mkString("Coll(", ", ", ")"))
    println("Register 3 (creation height): " + box.getCreationHeight)

  }

  def printRawRegisters(): Unit = {

    println("Printing raw register data")

    printReservedRegisters()

    for (i <- 0 until registers.size) {

      val regIndex: Int = i + 4
      val reg = registers(i)
      val decodedValue: String = reg.toString

      println(s"Register $regIndex: $decodedValue")

    }

  }
  def printDecodedRegisters(): Unit

  protected def processCollection(coll: Any)(implicit tag: ClassTag[_]): String = {

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
