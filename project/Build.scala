import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "deokure",
    version := "0.1",
    versionCode := 0,
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
      keyalias in Android := "change-me",
      resolvers ++= Seq (
        "twitter4j.org Repository" at "http://twitter4j.org/maven2"
      ),
      libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "1.8.RC1" % "test",
        "org.twitter4j" % "twitter4j-core" % "3.0.0-SNAPSHOT"
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
