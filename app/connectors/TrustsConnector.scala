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

import config.AppConfig
import models.http.TaxableMigrationFlag
import models.{MigratingTrustDetails, NonMigratingTrustDetails, TrustDetailsType}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TrustsConnector @Inject()(http: HttpClientV2, config: AppConfig) {

  private val trustsUrl: String = s"${config.trustsUrl}/trusts"
  private val baseUrl: String = s"$trustsUrl/trust-details"

  def getTrustDetails(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TrustDetailsType] = {
    val fullUrl = s"$baseUrl/$identifier/transformed"
    http.get(url"$fullUrl").execute[TrustDetailsType]
    //http.GET[TrustDetailsType](url)
  }

  def setUkProperty(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/uk-property"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
    //http.PUT[Boolean, HttpResponse](url, value)
  }

  def setTrustRecorded(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/recorded"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
//    http.PUT[Boolean, HttpResponse](url, value)
  }

  def setUkRelation(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/uk-relation"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
   // http.PUT[Boolean, HttpResponse](url, value)
  }

  def setUkResident(identifier: String, value: Boolean)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/uk-resident"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
   // http.PUT[Boolean, HttpResponse](url, value)
  }

  def getTrustMigrationFlag(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[TaxableMigrationFlag] = {
    val fullUrl = s"$trustsUrl/$identifier/taxable-migration/migrating-to-taxable"
    http.get(url"$fullUrl").execute[TaxableMigrationFlag]
    //http.GET[TaxableMigrationFlag](url)
  }

  def setMigratingTrustDetails(identifier: String, value: MigratingTrustDetails)
                              (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/migrating-trust-details"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
   // http.PUT[MigratingTrustDetails, HttpResponse](url, value)
  }

  def setNonMigratingTrustDetails(identifier: String, value: NonMigratingTrustDetails)
                                 (implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/non-migrating-trust-details"
    http.put(url"$fullUrl").withBody(Json.toJson(value)).execute[HttpResponse]
    //http.PUT[NonMigratingTrustDetails, HttpResponse](url, value)
  }

  def wasTrustRegisteredWithDeceasedSettlor(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Boolean] = {
    val fullUrl = s"$baseUrl/$identifier/has-deceased-settlor"
    http.get(url"$fullUrl").execute[Boolean]
   // http.GET[Boolean](url)
  }

  def getTrustName(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[String] = {
    val fullUrl = s"$baseUrl/$identifier/trust-name"
    http.get(url"$fullUrl").execute[String]
   // http.GET[String](url)
  }

  def removeTrustTypeDependentTransformFields(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$trustsUrl/$identifier/trust-type-dependent-transform-fields"
    http.delete(url"$fullUrl").execute[HttpResponse]
    //http.DELETE[HttpResponse](url)
  }

  def removeOptionalTrustDetailTransforms(identifier: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    val fullUrl = s"$baseUrl/$identifier/optional-trust-detail-transforms"
    http.delete(url"$fullUrl").execute[HttpResponse]
   // http.DELETE[HttpResponse](url)
  }

}
