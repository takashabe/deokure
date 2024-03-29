import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "deokure",
    version := "1.0",
    versionCode := 1,
    scalaVersion := "2.9.2",
    platformName in Android := "android-14"
  )

  val proguardSettings = Seq (
    useProguard in Android := true,
    proguardOption in Android := """-dontnote **
                                   |-keep class twitter4j.** {*;}""".stripMargin
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "alias_name",
      resolvers ++= Seq (
        "twitter4j.org Repository" at "http://twitter4j.org/maven2",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
      ),
      libraryDependencies ++= Seq (
        "org.twitter4j" % "twitter4j-core" % "[2.2,2.2.9]",
        "org.specs2" %% "specs2" % "[1.1,)" % "test"
      )
    )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "deokure",
    file("."),
    settings = General.fullAndroidSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++
               AndroidTest.androidSettings ++
               General.proguardSettings ++ Seq (
      name := "deokureTests"
    )
  ) dependsOn main
}
