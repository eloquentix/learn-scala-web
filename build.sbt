name := """hello-scala"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % "4.1.6.Final",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)


fork in run := true