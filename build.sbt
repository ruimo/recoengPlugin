name := """recoengPlugin"""

organization := "com.ruimo.recoengplugin"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

crossScalaVersions := List("2.10.4", "2.11.2") 

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

publishTo := Some(
  Resolver.file(
    "recoeng",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
  )
)