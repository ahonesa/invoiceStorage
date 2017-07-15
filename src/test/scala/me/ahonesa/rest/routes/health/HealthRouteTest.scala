package me.ahonesa.rest.routes.health

import akka.http.scaladsl.testkit.ScalatestRouteTest
import me.ahonesa.rest.http.HttpService
import org.scalatest.{FunSuite, Matchers, WordSpec}


class HealthRouteTest extends WordSpec with Matchers with ScalatestRouteTest {

  val test = HealthRoute()

  "HealthRoute" should {
    "return Hello, World!" in {
      // tests:
      Get("/health") ~> test.route ~> check {
        responseAs[String] shouldEqual """"Hello, world!""""
      }
    }
  }
}
