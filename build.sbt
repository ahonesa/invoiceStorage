name := "invoiceStorage"
organization := "me.ahonesa"
version := "0.0.1"
scalaVersion := "2.11.8"

libraryDependencies ++= {

  val akka = "10.0.9"
  val phantom = "2.12.1"
  val scalaTest = "3.0.1"
  val slf4j = "1.7.25"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akka,
    "com.typesafe.akka" %% "akka-http" % akka,
    "com.typesafe.akka" %% "akka-http-spray-json" % akka,
    "com.outworkers"  %% "phantom-dsl" % phantom,
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.1",
    "org.scalatest" %% "scalatest" % scalaTest,
    "com.typesafe.akka" %% "akka-http-testkit" % akka
  )
}

Revolver.settings

enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

