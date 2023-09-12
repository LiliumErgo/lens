package configs.nft_config

import com.google.gson.{Gson, GsonBuilder}

import java.io.{File, FileReader}
import scala.util.Try

case class LensNFTConfig(
                          name: String,
                          description: String,
                          image: String,
                          imageSHA256: String,
                          attributes: Array[NFTAttribute],
                          levels: Array[NFTLevel],
                          stats: Array[NFTStat],
                          assetType: String,
                          explicit: Boolean
                        ) {

  def getNFTName: String = this.name

  def getNFTDescription: String = this.description

  def getImageHash: String = this.imageSHA256

  def getImageLink: String = this.image

  def getAssetType: Array[Byte] = {

    this.assetType match {

      case "picture" => Array(1.toByte, 1.toByte)
      case "audio" => Array(1.toByte, 2.toByte)
      case "collection" => Array(1.toByte, 4.toByte)
      case "attachments" => Array(1.toByte, 15.toByte)
      case "membership" => Array(2.toByte, 1.toByte)

    }

  }

  def getArtworkStandardVersion: Int = 2

  def getProperties: Array[(String, String)] = {

    val attributes: Array[NFTAttribute] = this.attributes
    val formatted: Array[(String, String)] = attributes.map(attribute => (attribute.trait_type, attribute.value))
    formatted

  }

  def getLevels: Array[(String, (Int, Int))] = {

    val levels: Array[NFTLevel] = this.levels
    val formatted: Array[(String, (Int, Int))] = levels.map(level => (level.trait_type, (Integer.getInteger(level.value).toInt, Integer.getInteger(level.max_value).toInt)))
    formatted

  }

  def getStats: Array[(String, (Int, Int))] = {

    val stats: Array[NFTStat] = this.stats
    val formatted: Array[(String, (Int, Int))] = stats.map(stat => (stat.trait_type, (Integer.getInteger(stat.value).toInt, Integer.getInteger(stat.max_value).toInt)))
    formatted

  }

  def getAdditionalInfo: Array[(String, String)] = {

    val explicitByte: Byte = if (this.explicit) 1.toByte else 0.toByte

    Array(
      ("explicit", explicitByte.toHexString)
    )

  }

}

object LensNFTConfig {

  def load(configFilePath: String): Try[Array[LensNFTConfig]] = Try {

    // Load the file
    val configFile: File = new File(configFilePath)

    // Read the file
    val configReader: FileReader = new FileReader(configFile)

    // Create Gson object to parse json
    val gson: Gson = new GsonBuilder().create()

    // Parse the json and create the config object
    val config: Array[LensNFTConfig] = gson.fromJson[Array[LensNFTConfig]](configReader, classOf[Array[LensNFTConfig]])
    configReader.close()
    config

  }

}
