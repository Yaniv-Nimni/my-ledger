ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val zioVersion = "1.0.17"
lazy val zioMagicVersion = "0.3.11"
lazy val zioLoggingVersion = "0.5.14"
lazy val sttpVersion = "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "ledger",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-streams" % zioVersion,
      "io.github.kitlangton" %% "zio-magic" % zioMagicVersion,
      "dev.zio" %% "zio-test" % zioVersion, // adding zio-test in the regular scope, because zio-prelude and zio-magic depend on it
      "dev.zio" %% "zio-logging" % zioLoggingVersion,
      "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion,
      "dev.zio" %% "zio-json" % "0.2.0",
      "io.d11" %% "zhttp" % "1.0.0.0-RC27",
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio1" % sttpVersion,
      "com.softwaremill.sttp.client3" %% "circe" % sttpVersion
    )
  )
