name := "invoiceStorage"
organization := "me.ahonesa"
version := "0.0.1"
scalaVersion := "2.11.8"

libraryDependencies ++= {

  val akka = "10.0.9"
  val phantom = "2.12.1"
  val scalaTest = "3.0.1"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akka,
    "com.typesafe.akka" %% "akka-http" % akka,
    "com.typesafe.akka" %% "akka-http-spray-json" % akka,
    "com.outworkers"  %% "phantom-dsl" % phantom,
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.9.1",
    "io.swagger" % "swagger-jaxrs" % "1.5.15",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.scalatest" %% "scalatest" % scalaTest % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akka % "test"
  )
}

Revolver.settings

enablePlugins(DockerPlugin)
enablePlugins(JavaAppPackaging)

