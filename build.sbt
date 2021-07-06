name := """websocket-scala-play"""
organization := "com.praphull"
description := "Websockets in Scala & Play Framework"
version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtWeb)
  .settings(
    // Required for Asset versioning
    pipelineStages := Seq(digest, gzip)
  )

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

)


