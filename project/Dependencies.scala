import sbt._

object Version {
  val hadoop    = "2.7.1"
  val logback   = "1.1.3"
  val mockito   = "1.10.19"
  val scala     = "2.11.6"
  val scalaTest = "2.2.4"
  val slf4j     = "1.7.12"
  val spark     = "1.4.1"
  val apacheTika     = "1.10"
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
