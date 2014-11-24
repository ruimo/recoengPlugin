name := "recoengPlugin4Play22"

version := "1.0-SNAPSHOT"

organization := "com.ruimo"

resolvers += "ruimo.com" at "http://static.ruimo.com/release"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.ruimo" %% "recoengcommon" % "1.1-SNAPSHOT",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)     

publishTo := Some(
  Resolver.file(
    "recoeng",
    new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
  )
)

play.Project.playScalaSettings
