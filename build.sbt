name := "hello-slick"

version := "1.0"

mainClass in Compile := Some("HelloSlick")

libraryDependencies ++= List(
  TypesafeLibrary.slick.value,
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.175",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test"
)
