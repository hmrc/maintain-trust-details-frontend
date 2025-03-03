/*
 * Copyright 2024 HM Revenue & Customs
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

package utils

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryListRow, Value}
import viewmodels.{AnswerRow, AnswerSection}

object SectionFormatter {

  def formatAnswerSection(section: AnswerSection)(implicit messages: Messages): Seq[SummaryListRow] = {
    section.rows.zipWithIndex.map {
      case (row: AnswerRow, i: Int) =>
        SummaryListRow(
          key = Key(classes = "govuk-!-width-two-thirds", content = Text(messages(row.label, row.labelArg))),
          value = Value(classes = "govuk-!-width-one-half", content = HtmlContent(row.answer)),
          actions = Option.when(row.canEdit)
          {
            Actions(items = Seq(ActionItem(
            href = row.changeUrl.getOrElse(""),
            classes = s"change-link-$i",
            visuallyHiddenText = Some(messages(row.label)),
            content = Text(messages("site.edit"))
          )))
          }
        )
    }
  }
}
