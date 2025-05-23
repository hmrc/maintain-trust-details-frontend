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

package connectors

import com.google.inject.ImplementedBy
import config.AppConfig
import models.http.{TrustsAuthInternalServerError, TrustsAuthResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import utils.SessionLogging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrustsAuthConnectorImpl])
trait TrustsAuthConnector {
  def agentIsAuthorised()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse]
  def authorisedForIdentifier(identifier: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse]
}

class TrustsAuthConnectorImpl @Inject()(http: HttpClientV2, config: AppConfig)
  extends TrustsAuthConnector with SessionLogging {

  private val baseUrl: String = s"${config.trustsAuthUrl}/trusts-auth"

  override def agentIsAuthorised()
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse] = {
    val fullUrl = s"$baseUrl/agent-authorised"
    http.get(url"$fullUrl").execute[TrustsAuthResponse].recover {
      case e =>
        warnLog(s"unable to authenticate agent due to an exception ${e.getMessage}")
        TrustsAuthInternalServerError
    }
  }

  override def authorisedForIdentifier(identifier: String)
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrustsAuthResponse] = {
    val fullUrl = s"$baseUrl/authorised/$identifier"
    http.get(url"$fullUrl").execute[TrustsAuthResponse].recover {
      case e =>
        warnLog(s"unable to authenticate organisation for $identifier due to an exception ${e.getMessage}", Some(identifier))
        TrustsAuthInternalServerError
    }
  }
}
