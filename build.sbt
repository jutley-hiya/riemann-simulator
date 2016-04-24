import sbt._
import Keys._

name := "RiemannEventGenerator"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "clojars.org" at "http://clojars.org/repo"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.3",
  "com.aphyr" % "riemann-java-client" % "0.3.1",
  "org.slf4j" % "slf4j-simple" % "1.7.21"
)