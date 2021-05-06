/*
 * Copyright 2021 HM Revenue & Customs
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

package models

import play.api.libs.json.{JsError, JsString, JsSuccess, Reads, Writes}

sealed trait TypeOfTrust {
  val asString: String
}

object TypeOfTrust {

  case object WillTrustOrIntestacyTrust extends TypeOfTrust {
    override val asString: String = "Will Trust or Intestacy Trust"
  }

  case object DeedOfVariationTrustOrFamilyArrangement extends TypeOfTrust {
    override val asString: String = "Deed of Variation Trust or Family Arrangement"
  }

  case object InterVivosSettlement extends TypeOfTrust {
    override val asString: String = "Inter vivos Settlement"
  }

  case object EmploymentRelated extends TypeOfTrust {
    override val asString: String = "Employment Related"
  }

  case object HeritageMaintenanceFund extends TypeOfTrust {
    override val asString: String = "Heritage Maintenance Fund"
  }

  case object FlatManagementCompanyOrSinkingFund extends TypeOfTrust {
    override val asString: String = "Flat Management Company or Sinking Fund"
  }

  implicit val reads: Reads[TypeOfTrust] = Reads {
    case JsString(WillTrustOrIntestacyTrust.asString) => JsSuccess(WillTrustOrIntestacyTrust)
    case JsString(DeedOfVariationTrustOrFamilyArrangement.asString) => JsSuccess(DeedOfVariationTrustOrFamilyArrangement)
    case JsString(InterVivosSettlement.asString) => JsSuccess(InterVivosSettlement)
    case JsString(EmploymentRelated.asString) => JsSuccess(EmploymentRelated)
    case JsString(HeritageMaintenanceFund.asString) => JsSuccess(HeritageMaintenanceFund)
    case JsString(FlatManagementCompanyOrSinkingFund.asString) => JsSuccess(FlatManagementCompanyOrSinkingFund)
    case _ => JsError("Invalid type of trust")
  }

  implicit val writes: Writes[TypeOfTrust] = Writes(x => JsString(x.asString))

}
