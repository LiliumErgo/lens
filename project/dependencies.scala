import sbt._

object dependencies {

  val Ergo: List[ModuleID] = List(
    "org.scorexfoundation" %% "scrypto" % "2.3.0",
    "org.ergoplatform" %% "ergo-appkit" % "5.0.2",
    "org.scorexfoundation" %% "sigma-state" % "5.0.5"
  )

  val GuapSwap: List[ModuleID] = List(
    "org.guapswap" %% "sigma-builders" % "1.0.3"
  )

  val Testing: List[ModuleID] = List(
    "org.scalactic" %% "scalactic" % "3.2.15",
    "org.scalatest" %% "scalatest" % "3.2.15" % "test"
  )

  val Sttp: List[ModuleID] = List(
    "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M4",
    "com.softwaremill.sttp.client4" %% "circe" % "4.0.0-M4"
  )

  val Circe: List[ModuleID] = List(
    "io.circe" %% "circe-generic" % "0.14.5"
  )

}