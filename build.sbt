// Note: settings common to all subprojects are defined in project/GlobalPlugin.scala

// The root project is implicit, so we don't have to define it.
// We do need to prevent publishing for it, though:
import sbt._

lazy val root = Project("custome-avro-converter", file("."))
  .settings(
    publish := {},
    publishArtifact := false,
    name := "custome-avro-converter"
  )
  .aggregate(
    `core-module`
  )

val `core-module` = project.in(file("core-module"))
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.magnolia" % "magnolia-core_3" % MagnoliaVersion
        )
  )
