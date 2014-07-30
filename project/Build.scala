import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "recoengPlugin"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "ruimo.com",
    publishTo := Some(
      Resolver.file(
        "recoeng",
        new File(Option(System.getenv("RELEASE_DIR")).getOrElse("/tmp"))
      )
    )
  )
}
