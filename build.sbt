name := """play-scala-template"""
organization := "com.praphull"
description := "Play Scala Template"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

)

// Required for Asset versioning
pipelineStages in ThisBuild := Seq(digest, gzip)

