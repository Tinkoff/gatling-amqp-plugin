import sbt._

object Dependencies {
  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling" % "gatling-test-framework"
  ).map(_ % "3.4.0" % Compile)
  
  lazy val rabbitmq = "com.rabbitmq" % "amqp-client" % "5.9.0"
  lazy val commonsPool = "org.apache.commons" % "commons-pool2" % "2.9.0"
  
}
