# Gatling AMQP Plugin 

![Build](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/workflows/Build/badge.svg) [![Maven Central](https://img.shields.io/maven-central/v/ru.tinkoff/gatling-amqp-plugin_2.13.svg?color=success)](https://search.maven.org/search?q=ru.tinkoff.gatling-amqp-plugin) [![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

Plugin for support performance testing with AMQP in Gatling(3.9.x)

# Usage

## Getting Started
Plugin is currently available for Scala 2.13, Java 17, Kotlin.

You may add plugin as dependency in project with your tests. 

### Scala

Write this to your build.sbt: 

``` scala
libraryDependencies += "ru.tinkoff" %% "gatling-amqp-plugin" % <version> % Test
``` 

### Java

Write this to your dependencies block in build.gradle:

```java
gatling "ru.tinkoff:gatling-amqp-plugin_2.13:<version>"
```

### Kotlin

Write this to your dependencies block in build.gradle:

```kotlin
gatling("ru.tinkoff:gatling-amqp-plugin_2.13:<version>")
```

## Example Scenarios

### Scala 

* Example scenario for [publishing](src/test/scala/ru/tinkoff/gatling/amqp/examples/PublishExample.scala)
* Example scenario for [Publish And Reply](src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyExample.scala)
* Example scenario for [Publish and Reply on different message-brokers](src/test/scala/ru/tinkoff/gatling/amqp/examples/RequestReplyTwoBrokerExample.scala)

### Java

* Example scenario for [publishing](src/test/java/ru/tinkoff/gatling/javaapi/amqp/examples/PublishExample.java)
* Example scenario for [Publish And Reply](src/test/java/ru/tinkoff/gatling/javaapi/amqp/examples/RequestReplyExample.java)
* Example scenario for [Publish and Reply on different message-brokers](src/test/java/ru/tinkoff/gatling/javaapi/amqp/examples/RequestReplyTwoBrokerExample.java)

### Kotlin

* Example scenario for [publishing](src/test/kotlin/ru/tinkoff/gatling/javaapi/amqp/examples/PublishExample.kt)
* Example scenario for [Publish And Reply](src/test/kotlin/ru/tinkoff/gatling/javaapi/amqp/examples/RequestReplyExample.kt)
* Example scenario for [Publish and Reply on different message-brokers](src/test/kotlin/ru/tinkoff/gatling/javaapi/amqp/examples/RequestReplyTwoBrokerExample.kt)