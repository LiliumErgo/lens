package configs.collection_config

import com.google.gson.{Gson, GsonBuilder}
import org.ergoplatform.appkit.Address
import org.guapswap.sigmabuilders

import java.io.{File, FileReader}
import scala.util.Try

case class LensCollectionConfig(
                           collectionName: String,
                           collectionDescription: String,
                           collectionCategory: String,
                           collectionLogoUrl: String,
                           collectionFeaturedImageUrl: String,
                           collectionBannerImageUrl: String,
                           collectionSize: Long,
                           royalties: Array[RoyaltyInfo],
                           socials: Array[SocialInfo],
                           mintingExpiry: Long,
                           additionalInformation: Array[AdditionalInfo]
                           ) {

  def getCollectionName: String = this.collectionName

  def getCollectionDescription: String = this.collectionDescription

  def getCollectionSize: Long = this.collectionSize

  def getCollectionRoyalties: Array[(Address, Int)] = {

    val royalties: Array[RoyaltyInfo] = this.royalties
    val formatted: Array[(Address, Int)] = royalties.map(royalty => (Address.create(royalty.royaltyAddress), royalty.royaltyPercentage))
    formatted

  }

  def getCollectionStandardVersion: Int = 2

  def getCollectionInfo: Array[String] = {

      Array(
        this.collectionLogoUrl,
        this.collectionFeaturedImageUrl,
        this.collectionBannerImageUrl,
        this.collectionCategory
      )

  }

  def getSocials: Array[(String, String)] = {

      val socials: Array[SocialInfo] = this.socials
      val formatted: Array[(String, String)] = socials.map(socialInfo => (socialInfo.socialName, socialInfo.socialUrl))
      formatted

  }

  def getMintingExpiry: Long = {
    this.mintingExpiry
  }

  def getAdditionalInfo: Array[(String, String)] = {

    val infos: Array[AdditionalInfo] = this.additionalInformation
    val formatted: Array[(String, String)] = infos.map(info => (info.infoKey, info.infoValue))
    formatted

  }

}

object LensCollectionConfig {

  def load(configFilePath: String): Try[LensCollectionConfig] = Try {

    // Load the file
    val configFile: File = new File(configFilePath)

    // Read the file
    val configReader: FileReader = new FileReader(configFile)

    // Create Gson object to parse json
    val gson: Gson = new GsonBuilder().create()

    // Parse the json and create the config object
    val config: LensCollectionConfig = gson.fromJson[LensCollectionConfig](configReader, classOf[LensCollectionConfig])
    configReader.close()
    config

  }

}
