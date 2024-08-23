import sbtassembly.AssemblyPlugin.autoImport.assembly

val scala3Version = "3.2.0"
val akkaVersion = "2.6.19"
val fakeLoadVersion = "0.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "spawner",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++=
      Seq("org.scalameta" %% "munit" % "0.7.29" % Test,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      //Akka cluster
        "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
          //Akka cluster sharding
        "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
//Akka persistence
"com.typesafe.akka" %% "akka-persistence" % akkaVersion,
"com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
"io.aeron" % "aeron-driver" % "1.37.0",
"io.aeron" % "aeron-client" % "1.37.0",
//Akka stream
"com.typesafe.akka" %% "akka-stream" % akkaVersion,
"com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
 "com.martensigwart" % "fakeload" % fakeLoadVersion

),
    assembly/mainClass := Some("Main.runIt")
  )



//SBT assembly properties
val jarName = "spawner.jar"
assembly/assemblyJarName := jarName


//Merging strategies
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}