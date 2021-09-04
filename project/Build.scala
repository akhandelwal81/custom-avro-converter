import sbt.Keys._
import sbt._

/** Adds common settings automatically to all subprojects */
object Build extends AutoPlugin {

  object autoImport {
    val org = "com.sksamuel.avro4s"
    val AvroVersion = "1.10.2"
    val Log4jVersion = "1.2.17"
    val ScalatestVersion = "3.2.9"
    val Slf4jVersion = "1.7.32"
    val Json4sVersion = "3.6.11"
    val CatsVersion = "2.6.1"
    val RefinedVersion = "0.9.26"
    val ShapelessVersion = "2.3.7"
    val MagnoliaVersion = "2.0.0-M9"
    val SbtJmhVersion = "0.3.7"
    val JmhVersion = "1.32"
  }

  import autoImport._

  def isGithubActions: Boolean = sys.env.getOrElse("CI", "false") == "true"
  def releaseVersion: String = sys.env.getOrElse("RELEASE_VERSION", "")
  def isRelease: Boolean = releaseVersion != ""
  def githubRunNumber: String = sys.env.getOrElse("GITHUB_RUN_NUMBER", "local")
  def ossrhUsername: String = sys.env.getOrElse("OSSRH_USERNAME", "")
  def ossrhPassword: String = sys.env.getOrElse("OSSRH_PASSWORD", "")
  def publishVersion: String = if (isRelease) releaseVersion else "5.0.0." + githubRunNumber + "-SNAPSHOT"

  override def trigger = allRequirements
  override def projectSettings = publishingSettings ++ Seq(
    organization := org,
    scalaVersion := "3.0.2",
    resolvers += Resolver.mavenLocal,
    parallelExecution in Test := false,
    javacOptions := Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "org.scala-lang"    % "scala3-compiler_3" % scalaVersion.value,
      "org.apache.avro"   % "avro"              % AvroVersion,
      "org.slf4j"         % "slf4j-api"         % Slf4jVersion          % "test",
      "log4j"             % "log4j"             % Log4jVersion          % "test",
      "org.slf4j"         % "log4j-over-slf4j"  % Slf4jVersion          % "test",
      "org.scalatest"     % "scalatest_3"       % ScalatestVersion      % "test"
    )
  )

  val publishingSettings = Seq(
    publishMavenStyle := true,
    Test / publishArtifact := false,
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "oss.sonatype.org",
      ossrhUsername,
      ossrhPassword
    ),
    version := publishVersion,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isRelease) {
        Some("releases" at s"${nexus}service/local/staging/deploy/maven2")
      } else {
        Some("snapshots" at s"${nexus}content/repositories/snapshots")
      }
    },
    pomExtra := {
      <url>https://github.com/akhandelwal/custome-avro-converter</url>
        <licenses>
          <license>
            <name>The Apache 2.0 License</name>
            <url>https://opensource.org/licenses/Apache-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:akhandelwal/custome-avro-converter.git</url>
          <connection>scm:git@github.com:akhandelwal/custome-avro-converter.git</connection>
        </scm>
        <developers>
          <developer>
            <id>akhandelwal</id>
            <name>akhandelwal</name>
            <url>http://github.com/akhandelwal</url>
          </developer>
        </developers>
    }
  )
}
