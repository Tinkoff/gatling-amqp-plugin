import sbt._

object Dependencies {
  val gatlingVersion = "3.9.0"

  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"      % gatlingVersion % Provided,
    "io.gatling" % "gatling-core-java" % gatlingVersion % Provided,
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "it,test",
    "io.gatling"            % "gatling-test-framework" % gatlingVersion % "it,test",
  )

  lazy val rabbitmq    = "com.rabbitmq"       % "amqp-client"   % "5.16.0"
  lazy val commonsPool = "org.apache.commons" % "commons-pool2" % "2.11.1"
  lazy val fastUUID    = "com.eatthepath"     % "fast-uuid"     % "0.2.0"

}
