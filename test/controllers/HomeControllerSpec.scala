package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.http.HeaderNames
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "POST /" should {
    "work" in {
      //val service = app.injector.instanceOf(classOf[MyService])

      val controller = new HomeController(stubControllerComponents())
      val requestBody = Json.obj("name" -> "Praphull")
      val response = testRequest(controller, requestBody)

      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      contentAsString(response) must include("Praphull")
    }
  }

  private def testRequest(controller: HomeController, requestBody: JsValue) = {
    controller.test().apply(FakeRequest(POST, "/test",
      FakeHeaders(Seq(HeaderNames.CONTENT_TYPE -> "application/json")),
      requestBody
    ))
  }
}
