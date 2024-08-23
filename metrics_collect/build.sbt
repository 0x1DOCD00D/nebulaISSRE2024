
val scala3Version = "3.2.0"
val firebaseSdkVersion = "3.0.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "metrics_collector",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "com.google.firebase" % "firebase-server-sdk" % firebaseSdkVersion
    ),
  )



//SBT assembly properties
val jarName = "metrics_collector.jar"
assembly/assemblyJarName := jarName


//Merging strategies
ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}