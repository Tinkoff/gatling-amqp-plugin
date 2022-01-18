package ru.tinkoff.gatling.amqp.checks

import io.gatling.commons.validation._
import io.gatling.core.check.{Extractor, _}
import io.gatling.core.session._
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

object AmqpResponseCodeCheckBuilder {

  private type AmqpCheckMaterializer[T, S] = CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, S]

  class NotInMatcher[A](expected: Seq[A]) extends Matcher[A] {

    def name: String = expected.mkString("notIn(", ",", ")")

    protected def doMatch(actual: Option[A]): Validation[Option[A]] =
      actual match {
        case Some(actualValue) =>
          if (!expected.contains(actualValue))
            actual.success
          else
            s"found $actualValue".failure
        case _                 => Validator.FoundNothingFailure
      }
  }

  class ExtendedDefaultFindCheckBuilder[T, P, X](ext: Expression[Extractor[P, X]], displayActualValue: Boolean)
      extends CheckBuilder.Find.Default[T, P, X](ext, displayActualValue) {
    def notIn(expected: Expression[Seq[X]]): CheckBuilder[T, P, X] =
      new CheckBuilder.Final[T, P, X](this.ext, expected.map(new NotInMatcher(_)), displayActualValue, None, None)

    def notIn(expected: X*): CheckBuilder[T, P, X] = notIn(expected.expressionSuccess)
  }

  trait AmqpMessageCheckType

  val ResponseCode: ExtendedDefaultFindCheckBuilder[AmqpMessageCheckType, AmqpProtocolMessage, String] = {
    val rcExtractor = new Extractor[AmqpProtocolMessage, String] {
      val name                                                             = "responseCode"
      val arity                                                            = "find"
      def apply(prepared: AmqpProtocolMessage): Validation[Option[String]] = prepared.responseCode.success
    }.expressionSuccess

    new ExtendedDefaultFindCheckBuilder(rcExtractor, displayActualValue = true)
  }
}
