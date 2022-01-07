/*
 * Copyright 2022 HM Revenue & Customs
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

package views.maintain

import forms.DateFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.maintain.EfrbsStartDateView

import java.time.LocalDate

class EfrbsStartDateViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "efrbsStartDate"

  val form: Form[LocalDate] = new DateFormProvider(frontendAppConfig).withPrefix(messageKeyPrefix)

  "EfrbsStartDateView" must {

    val view = viewFor[EfrbsStartDateView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form)(fakeRequest, messages)

    behave like normalPage(
      view = applyView(form),
      messageKeyPrefix = messageKeyPrefix,
      expectedGuidanceKeys = "hint"
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(
      form = form,
      createView = applyView,
      messageKeyPrefix = messageKeyPrefix,
      key = "value"
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
