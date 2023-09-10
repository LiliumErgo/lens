package configs.nft_config

import com.google.gson.{Gson, GsonBuilder}

import java.io.{File, FileReader}
import scala.util.Try

case class NFTAttribute(
                        trait_type: String,
                        value: String
                        ) {}
