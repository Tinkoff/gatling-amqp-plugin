package ru.tinkoff.gatling.amqp.checks

import io.gatling.commons.validation._
import io.gatling.core.check.{Extractor, _}
import io.gatling.core.session._
import ru.tinkoff.gatling.amqp.AmqpCheck
import ru.tinkoff.gatling.amqp.request.AmqpProtocolMessage

object AmqpResponseCodeCheckBuilder {
  trait AmqpMessageCheckType

  type AmqpCheckMaterializer[T, S] = CheckMaterializer[T, AmqpCheck, AmqpProtocolMessage, S]

  class NotInMatcher[A](expected: Seq[A]) extends Matcher[A] {

    def name: String = expected.mkString("notIn(", ",", ")")

    protected def doMatch(actual: Option[A]): Validation[Option[A]] = actual match {
      case Some(actualValue) =>
        if (!expected.contains(actualValue))
          actual.success
        else
          s"found $actualValue".failure
      case _ => Validator.FoundNothingFailure
    }
  }

  type CBWithSaveAs[T, P, X] = CheckBuilder[T, P, X] with SaveAs[T, P, X]

  class ExtendedDefaultFindCheckBuilder[T, P, X](ext: Expression[Extractor[P, X]], displayActualValue: Boolean)
      extends DefaultFindCheckBuilder[T, P, X](ext, displayActualValue) {
    def notIn(expected: Expression[Seq[X]]): CBWithSaveAs[T, P, X] =
      new CheckBuilder[T, P, X](this.ext, expected.map(new NotInMatcher(_)), displayActualValue) with SaveAs[T, P, X]

    def notIn(expected: X*): CBWithSaveAs[T, P, X] = notIn(expected.toSeq.expressionSuccess)
  }

  val ResponseCode: ExtendedDefaultFindCheckBuilder[AmqpMessageCheckType, AmqpProtocolMessage, String] = {
    val rcExtractor = new Extractor[AmqpProtocolMessage, String] {
      val name = "responseCode"
      val arity = "find"
      def apply(prepared: AmqpProtocolMessage): Validation[Option[String]] = prepared.responseCode.success
    }.expressionSuccess

    new ExtendedDefaultFindCheckBuilder(rcExtractor, displayActualValue = true)
  }
}
