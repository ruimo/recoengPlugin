name := """recoengPlugin4Play23"""

organization := "com.ruimo"

version := "1.0-SNAPSHOT"

resolvers += "ruimo.com" at "http://static.ruimo.com/release"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

crossScalaVersions := List("2.10.4", "2.11.4") 

libraryDependencies ++= Seq(
  "com.ruimo" %% "recoengcommon" % "1.1-SNAPSHOT",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
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
