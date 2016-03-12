name := "ScalaCheck Demo"
version := "0.0.1"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.0" % "test"
)
