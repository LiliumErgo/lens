import dependencies._

inThisBuild(List(
  organization := "io.liliumergo", // group id
  name := "lens", // artifact id
  version := "5.0.0",
  homepage := Some(url("https://liliumergo.io")),
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  description := "Super basic NFT debugging tool. ",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/LiliumErgo/lens"),
      "scm:git@github.com:LiliumErgo/lens.git"
    )
  ),
  developers := List(
    Developer(
      "lgd",
      "Luca D'Angelo",
      "ldgaetano@protonmail.com",
      url("https://github.com/lucagdangelo")
    )
  ),

  libraryDependencies ++=
    Ergo ++
    GuapSwap ++
    Testing
  ,

  resolvers := List(
    "Sonatype OSS Releases" at "https://s01.oss.sonatype.org/content/repositories/releases",
    "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
  ),

  versionScheme := Some ("semver-spec"),
  assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
  assembly / assemblyOutputPath := file(s"./${name.value}-${version.value}.jar/")


))