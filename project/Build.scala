import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "recoengPlugin"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.ruimo" %% "recoengcommon" % "1.1-SNAPSHOT",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    name := "recoengplugin",
    organization := "com.ruimo",
    resolvers += "ruimo.com" at "http://static.ruimo.com/release",
    publishTo := Some(
      Resolver.file(
        "recoeng",
        new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
      )
    )
  )
}
