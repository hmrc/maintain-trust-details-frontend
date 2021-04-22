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

package navigation

import javax.inject.Inject
import models.UserAnswers
import pages.Page
import pages.maintain.{BusinessRelationshipYesNoPage, TrustEEAYesNoPage, TrustOwnUKLandOrPropertyPage, TrustUKResidentPage}
import play.api.mvc.Call

class TrustDetailsNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, userAnswers: UserAnswers): Call =
    routes()(page)(userAnswers)

  private def simpleNavigation(): PartialFunction[Page, UserAnswers => Call] = {
    case TrustOwnUKLandOrPropertyPage => _ => controllers.maintain.routes.TrustEEAYesNoController.onPageLoad()
    case TrustEEAYesNoPage => ua => trustUKResidentPage(ua)
    case BusinessRelationshipYesNoPage => _ => controllers.maintain.routes.CheckDetailsController.onPageLoad()
  }


  def routes(): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation()


  private def trustUKResidentPage(ua: UserAnswers): Call = {
    if (ua.get(TrustUKResidentPage).contains(true)) {
      controllers.maintain.routes.CheckDetailsController.onPageLoad()
    } else {
      controllers.maintain.routes.BusinessRelationshipYesNoController.onPageLoad()
    }
  }
}
