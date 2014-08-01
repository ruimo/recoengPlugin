import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "recoengPlugin"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.ruimo" %% "recoengcommon" % "1.1-SNAPSHOT",
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    name := "recoengplugin",
    organization := "com.ruimo",
    resolvers += "ruimo.com" at "http://www.ruimo.com/release",
    publishTo := Some(
      Resolver.file(
        "recoeng",
        new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
      )
    )
  )
}
