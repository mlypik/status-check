organization in ThisBuild := "io.github.mlypik"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val idGenerator = "com.softwaremill.common" %% "id-generator" % "1.2.0"

lazy val `status-check` = (project in file("."))
  .aggregate(`status-check-api`, `status-check-impl`, `status-check-stream-api`, `status-check-stream-impl`, `jobservice-api`, `jobservice-impl`)

lazy val `status-check-api` = (project in file("status-check-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `status-check-impl` = (project in file("status-check-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`status-check-api`)

lazy val `status-check-stream-api` = (project in file("status-check-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `status-check-stream-impl` = (project in file("status-check-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .dependsOn(`status-check-stream-api`, `status-check-api`)

lazy val `jobservice-api` = (project in file("jobservice-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `jobservice-impl` = (project in file("jobservice-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      idGenerator,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`jobservice-api`)