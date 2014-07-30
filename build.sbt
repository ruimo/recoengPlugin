name := """recoengPlugin"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := Option(System.getProperty("SCALA_VERSION")).getOrElse("2.10.4")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

publishTo := Some(
  Resolver.file(
    "recoeng",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp") + "/com/ruimo/recoeng")
  )
)