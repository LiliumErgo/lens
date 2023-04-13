package protocol

import scala.collection.immutable.HashMap
import scala.util.{Try, Success, Failure}
import java.io.{File, FileNotFoundException}

import java.time.LocalDateTime
import java.time.ZoneId

/**
  * Object representing constants and methods relevant to GuapSwap.
  */
object MintForDummiesUtils {

  final val ERGO_EXPLORER_TX_URL_PREFIX: String = "https://explorer.ergoplatform.com/en/transactions/"

  /**
    * Get a time-zone timestamp.
    *
    * @param zone
    * @return A time-zone timestamp, with date and time.
    */
  def getTimeStamp(zone: String): String = {

    // Get the date and time in UTC format
    val dateTime: LocalDateTime = LocalDateTime.now(ZoneId.of(zone))

    // Format the time string 
    val date: String = dateTime.toString().split("[T]")(0)
    val time: String = dateTime.toString().split("[T]")(1).split("\\.")(0)
    val timestamp: String = s"[${zone} ${date} ${time}]"

    timestamp
  }
}