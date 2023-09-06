package utils

import org.ergoplatform.explorer.client.model.OutputInfo
import org.ergoplatform.explorer.client.{DefaultApi, ExplorerApiClient}

import scala.collection.JavaConverters._

case class ExplorerApi(
                        nodeUrl: String,
                        explorerUrl: String
                      ) {

  private val explorerApi: DefaultApi = new ExplorerApiClient(this.explorerUrl).createService(classOf[DefaultApi])

  def getUnspentBoxesByTokenID(tokenId: String, offset: Integer, limit: Integer): Array[OutputInfo] = {

    val res = this.explorerApi.getApiV1BoxesUnspentBytokenidP1(tokenId, offset, limit).execute().body()

    try {
      res.getItems.asScala.toArray
    } catch {
      case e: Exception => null
    }

  }

  def getBoxesByTokenID(tokenId: String, offset: Integer, limit: Integer): Array[OutputInfo] = {

    val res = this.explorerApi.getApiV1BoxesBytokenidP1(tokenId, offset, limit).execute().body()

    try {
      res.getItems.asScala.toArray
    } catch {
      case e: Exception => null
    }

  }

  def getBoxesByErgoTree(ergotree: String, offset: Integer, limit: Integer): Array[OutputInfo] = {

    val res = this.explorerApi.getApiV1BoxesByergotreeP1(ergotree, offset, limit).execute().body()

    try {
      res.getItems.asScala.toArray
    } catch {
      case e: Exception => null
    }

  }

//  def getLatestBoxByTokenID(tokenId: String): OutputInfo = { // returns latest box the token has been in
//
//    var res = this.explorerApi.getApiV1BoxesBytokenidP1(tokenId, 0, 1).execute().body()
//
//    val offset = res.getTotal - 1
//    res = this.explorerApi.getApiV1BoxesBytokenidP1(tokenId, offset, 1).execute().body()
//
//    try {
//      res.getItems.get(0)
//    } catch {
//      case e: Exception => println(e); null
//    }
//
//  }

  //  def getBoxesfromTransaction(txId: String): TransactionInfo = {
  //    val api = this.getExplorerApi(this.apiUrl)
  //    api.getApiV1TransactionsP1(txId).execute().body()
  //  }
  //
  //  def getUnspentBoxesByAddress(address: String): util.List[OutputInfo] = {
  //    val api = this.getExplorerApi(this.apiUrl)
  //    api.getApiV1BoxesByaddressP1(address, 0, 100).execute().body().getItems
  //  }

}
