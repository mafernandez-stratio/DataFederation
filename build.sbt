name := "ProyectoFinal"

version := "1.0"

scalaVersion := "2.12.2"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.2" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.5.2",
  "com.typesafe.akka" % "akka-cluster-metrics_2.12" % "2.5.2",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.2",
  "org.apache.kafka" % "kafka-clients" % "0.10.2.1"
)
