import sbt._

object Version {
  val hadoop    = "2.6.0"
  val logback   = "1.1.2"
  val mockito   = "1.10.17"
  val scala     = "2.11.4"
  val scalaTest = "2.2.3"
  val slf4j     = "1.7.6"
  val spark     = "1.4.1"
  val apacheTika     = "1.9"
}

object Library {
  val hadoopClient   = "org.apache.hadoop" %  "hadoop-client"   % Version.hadoop
  val logbackClassic = "ch.qos.logback"    %  "logback-classic" % Version.logback
  val mockitoAll     = "org.mockito"       %  "mockito-all"     % Version.mockito
  val scalaTest      = "org.scalatest"     %% "scalatest"       % Version.scalaTest
  val slf4jApi       = "org.slf4j"         %  "slf4j-api"       % Version.slf4j
  val sparkCore      = "org.apache.spark"  %% "spark-core"      % Version.spark
  val sparkMl        = "org.apache.spark"  %% "spark-mllib"     % Version.spark
  val apacheTika     = "org.apache.tika"    %  "tika-parsers"   % Version.apacheTika
}

object Dependencies {

  import Library._

  val sparkTika = Seq(
    apacheTika,
    sparkCore,
    sparkMl,
    hadoopClient,
    logbackClassic % "test",
    scalaTest      % "test",
    mockitoAll     % "test"
  )
}
