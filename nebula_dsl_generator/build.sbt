val scala3Version = "3.1.3"
val configVersion = "1.4.2"
val playJsonVersion = "2.10.0-RC6"
val logBackVersion = "1.2.11"
val scalaLoggingVersion = "3.9.4"
val scalaNlpVersion = "2.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nebula_dsl_generator",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++=
      Seq(
        "org.scalameta" %% "munit" % "0.7.29" % Test,
        "com.typesafe.play" %% "play-json" % playJsonVersion,
        "com.typesafe" % "config" % configVersion,
        "ch.qos.logback" % "logback-classic" % logBackVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
        "org.scalanlp" %% "breeze" % scalaNlpVersion
      )
  )
