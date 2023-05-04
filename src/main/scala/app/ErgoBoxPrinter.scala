package app

import org.ergoplatform.appkit.{ErgoValue, InputBox}
import scala.collection.JavaConverters._

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

}
