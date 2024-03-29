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

package views.maintain

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.maintain.BeforeYouContinueView

class BeforeYouContinueViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "beforeYouContinue"

  "BeforeYouContinueView view" must {

    val view = viewFor[BeforeYouContinueView](Some(emptyUserAnswers))

    def applyView(): HtmlFormat.Appendable =
      view.apply()(fakeRequest, messages)

    behave like normalPageTitleWithCaption(
      view = applyView(),
      messageKeyPrefix = messageKeyPrefix,
      messageKeyParam = "",
      captionParam = "",
      expectedGuidanceKeys = "subheading", "bullet1", "bullet2", "bullet3"
    )

    behave like pageWithBackLink(applyView())

    behave like pageWithASubmitButton(applyView())
  }
}
