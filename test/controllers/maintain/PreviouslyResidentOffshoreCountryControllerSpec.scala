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

package controllers.maintain

import base.SpecBase
import forms.CountryFormProvider
import navigation.Navigator
import org.scalatestplus.mockito.MockitoSugar
import pages.maintain.PreviouslyResidentOffshoreCountryPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.{CountryOptions, InputOption}
import views.html.maintain.PreviouslyResidentOffshoreCountryView

class PreviouslyResidentOffshoreCountryControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new CountryFormProvider()
  val form: Form[String] = formProvider.withPrefix("previouslyResidentOffshoreCountry")

  lazy val previouslyResidentOffshoreCountryRoute: String = routes.PreviouslyResidentOffshoreCountryController.onPageLoad().url

  val validAnswer: String = "FR"

  val countryOptions: Seq[InputOption] = injector.instanceOf[CountryOptions].nonUkOptions

  "PreviouslyResidentOffshoreCountryController" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, previouslyResidentOffshoreCountryRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PreviouslyResidentOffshoreCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(PreviouslyResidentOffshoreCountryPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, previouslyResidentOffshoreCountryRoute)

      val view = application.injector.instanceOf[PreviouslyResidentOffshoreCountryView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), countryOptions)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[Navigator].toInstance(fakeNavigator))
        .build()

      val request = FakeRequest(POST, previouslyResidentOffshoreCountryRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }


    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(POST, previouslyResidentOffshoreCountryRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PreviouslyResidentOffshoreCountryView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, previouslyResidentOffshoreCountryRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, previouslyResidentOffshoreCountryRoute)
        .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
