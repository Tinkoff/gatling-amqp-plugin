# Introduction

Plugin for support performance testing with AMQP in Gatling(3.x.x)

# Usage

## Build 
For local usage clone project from github repository and run in project folder `sbt publishLocal`. 

After that you may include plugin as dependency in project with your tests. Write 
`libraryDependencies += "ru.tinkoff" %% "gatling-amqp-plugin" % <version> % Test` in build.sbt

## Example Scenarios
Example scenarios for publishing and request-reply exchanges you could be found [here](https://github.com/TinkoffCreditSystems/gatling-amqp-plugin/tree/master/src/test/scala/ru/tinkoff/gatling/amqp/examples/)