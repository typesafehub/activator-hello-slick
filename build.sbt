name := "hello-slick"

version := "1.0"

scalaVersion := "2.11.6"

mainClass in Compile := Some("HelloSlick")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170"
)
