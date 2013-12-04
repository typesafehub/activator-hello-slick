name := "hello-slick"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.0.0-M3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170"
)
