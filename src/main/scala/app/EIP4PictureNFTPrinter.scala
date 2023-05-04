package app

import org.ergoplatform.appkit.{ErgoClient, NetworkType}

case class EIP4PictureNFTPrinter(ergoClient: ErgoClient, networkType: NetworkType, issuanceBoxId: String) extends EIP4IssuanceBoxPrinter(ergoClient, networkType, issuanceBoxId) {


  override protected def printRegister8(): Unit = {

    val reg = registers(4)
    val regValue = reg.getValue
    val classTag = reg.getType.getRType.classTag
    val decodedHashString: String = processHash(regValue)(classTag)

    println("Register 8 (sha256 picture hash): " + decodedHashString)

  }

  override protected def printRegister9(): Unit = {

    val reg = registers(5)
    val regValue = reg.getValue
    val classTag = reg.getType.getRType.classTag
    val decodedLink: String = processStringCollection(regValue)(classTag)

    println("Register 9 (picture link): " + decodedLink)

  }

}
