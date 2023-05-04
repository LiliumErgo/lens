package app

import org.ergoplatform.appkit.{ErgoClient, Iso, JavaHelpers, NetworkType}
import special.collection.Coll

import java.nio.charset.Charset

case class EIP4PictureNFTPrinter(ergoClient: ErgoClient, networkType: NetworkType, issuanceBoxId: String) extends EIP4IssuanceBoxPrinter(ergoClient, networkType, issuanceBoxId) {


  override protected def printRegister8(): Unit = {

    val reg8 = registers(4)
    val decodedHashString: String = reg8.getValue match {
      case hash: Coll[Byte] =>
        val hashByteArray: Array[Byte] = JavaHelpers.collToByteArray(hash)
        val hashString: String = hashByteArray.map("%02x".format(_)).mkString
        hashString
    }

    println("Register 8 (sha256 picture hash): " + decodedHashString)

  }

  override protected def printRegister9(): Unit = {

    val reg9 = registers(5)
    val decodedLink: String = reg9.getValue match {
      case link: Coll[Byte] =>
        val linkByteArray: Array[Byte] = JavaHelpers.collToByteArray(link)
        val linkString: String = new String(linkByteArray, Charset.defaultCharset())
        linkString
    }

    println("Register 9 (picture link): " + decodedLink)

  }

}
