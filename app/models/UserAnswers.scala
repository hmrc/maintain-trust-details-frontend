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

import pages.QuestionPage
import play.api.Logging
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json._
import queries.{Gettable, Settable}
import utils.RichJson._

import java.time.LocalDateTime
import scala.util.{Failure, Success, Try}

case class UserAnswers(internalId: String,
                       identifier: String,
                       migratingFromNonTaxableToTaxable: Boolean = false,
                       data: JsObject = Json.obj(),
                       updatedAt: LocalDateTime = LocalDateTime.now) extends Logging {

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] = {
    Reads.at(page.path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_) => None
    }
  }

  def set[A](page: Settable[A], value: Option[A])(implicit writes: Writes[A]): Try[UserAnswers] = {
    value match {
      case Some(v) => setValue(page, v)
      case None => page.cleanup(value, this)
    }
  }

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = setValue(page, value)

  private def setValue[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {
    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        val errorPaths = errors.collectFirst { case (path, e) => s"$path $e" }
        logger.warn(s"Unable to set path ${page.path} due to errors $errorPaths")
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](query: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(query.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        query.cleanup(None, updatedAnswers)
    }
  }

  def deleteAtPath(path: JsPath): Try[UserAnswers] = {
    data.removeObject(path).map(obj => copy(data = obj)).fold(
      _ => Success(this),
      result => Success(result)
    )
  }

  def removePageIfConditionMet[T](page: QuestionPage[T], condition: Boolean): Try[UserAnswers] = {
    if (condition) {
      this.remove(page)
    } else {
      Success(this)
    }
  }

}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = (
    (__ \ "internalId").read[String] and
      (__ \ "identifier").read[String] and
      (__ \ "migratingFromNonTaxableToTaxable").readWithDefault[Boolean](false) and
      (__ \ "data").read[JsObject] and
      (__ \ "updatedAt").read(MongoDateTimeFormats.localDateTimeRead)
    )(UserAnswers.apply _)

  implicit lazy val writes: Writes[UserAnswers] = (
    (__ \ "internalId").write[String] and
      (__ \ "identifier").write[String] and
      (__ \ "migratingFromNonTaxableToTaxable").write[Boolean] and
      (__ \ "data").write[JsObject] and
      (__ \ "updatedAt").write(MongoDateTimeFormats.localDateTimeWrite)
    )(unlift(UserAnswers.unapply))

}
