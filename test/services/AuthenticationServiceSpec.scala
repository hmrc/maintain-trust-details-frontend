/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import base.SpecBase
import connectors.TrustsAuthConnector
import models.AgentUser
import models.http.{TrustsAuthAgentAllowed, TrustsAuthAllowed, TrustsAuthDenied, TrustsAuthInternalServerError}
import models.requests.DataRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{EitherValues, RecoverMethods}
import play.api.inject.bind
import play.api.mvc.AnyContent
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class AuthenticationServiceSpec extends SpecBase with ScalaFutures with EitherValues with RecoverMethods {

  private val utr = "0987654321"

  private val agentEnrolment = Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)
  private val trustsEnrolment = Enrolment("HMRC-TERS-ORG", List(EnrolmentIdentifier("SAUTR", utr)), "Activated", None)

  private val enrolments = Enrolments(Set(
    agentEnrolment,
    trustsEnrolment
  ))

  private implicit val hc: HeaderCarrier = HeaderCarrier()
  private implicit val dataRequest: DataRequest[AnyContent]
  = DataRequest[AnyContent](fakeRequest, emptyUserAnswers, AgentUser("internalId", enrolments, "SomeVal"))

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  private lazy val trustAuthConnector = mock[TrustsAuthConnector]

  "invoking authenticateForUtr" when {
    "user is authenticated" must {
      "return the data request" in {
        when(trustAuthConnector.authorisedForIdentifier(any())(any(), any())).thenReturn(Future.successful(TrustsAuthAllowed()))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateForIdentifier[AnyContent](utr)) {
          result =>
            result.value mustBe dataRequest
        }
      }
    }
    "user requires additional action" must {
      "redirect to desired url" in {
        when(trustAuthConnector.authorisedForIdentifier(any())(any(), any())).thenReturn(Future.successful(TrustsAuthDenied("some-url")))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateForIdentifier[AnyContent](utr)) {
          result =>
            val r = Future.successful(result.left.value)
            status(r) mustBe SEE_OTHER
            redirectLocation(r) mustBe Some("some-url")
        }
      }
    }
    "an internal server error is returned" must {
      "return an internal server error result" in {
        when(trustAuthConnector.authorisedForIdentifier(any())(any(), any())).thenReturn(Future.successful(TrustsAuthInternalServerError))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateForIdentifier[AnyContent](utr)) {
          result =>
            result.left.value.header.status mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

  }

  "invoking authenticateAgent" when {
    "user is authenticated" must {
      "return the data request" in {
        when(trustAuthConnector.agentIsAuthorised()(any(), any())).thenReturn(Future.successful(TrustsAuthAgentAllowed("SomeARN")))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateAgent()) {
          result =>
            result.value mustBe "SomeARN"
        }
      }
    }
    "user requires additional action" must {
      "redirect to desired url" in {
        when(trustAuthConnector.agentIsAuthorised()(any(), any())).thenReturn(Future.successful(TrustsAuthDenied("some-url")))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateAgent()) {
          result =>
            val r = Future.successful(result.left.value)
            status(r) mustBe SEE_OTHER
            redirectLocation(r) mustBe Some("some-url")
        }
      }
    }
    "an internal server error is returned" must {
      "return an internal server error result" in {
        when(trustAuthConnector.agentIsAuthorised()(any(), any())).thenReturn(Future.successful(TrustsAuthInternalServerError))

        val app = buildApp

        val service = app.injector.instanceOf[AuthenticationService]

        whenReady(service.authenticateAgent()) {
          result =>
            result.left.value.header.status mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

  }

  private def buildApp = {
    applicationBuilder()
      .overrides(bind[TrustsAuthConnector].toInstance(trustAuthConnector))
      .build()
  }
}
