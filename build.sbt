name := "hello-slick"

version := "1.0"

scalaVersion := "2.10.4"

mainClass in Compile := Some("HelloSlick")

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.1.0-M1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test"
)
