import sbt._

object Dependencies {
  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling" % "gatling-test-framework"
  ).map(_ % "3.1.2" % Compile)
  
  lazy val rabbitmq = "com.rabbitmq" % "amqp-client" % "5.7.1"
  lazy val commonsPool = "org.apache.commons" % "commons-pool2" % "2.6.2"
  
}
