package me.ahonesa.rest.routes.customers

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import me.ahonesa.core.models.{NewCustomer, Response}
import me.ahonesa.rest.services.CustomersService
import me.ahonesa.rest.utils.RestJsonFormats
import spray.json._
import io.swagger.annotations._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.{Failure, Success}

@Api(value = "/customers", description = "Hello Template.", produces = "application/json")
class CustomersRoute(customersService: CustomersService)(implicit executionContext: ExecutionContext) extends RestJsonFormats {

  import StatusCodes._

  val customersPath = "customers"

  val route = pathPrefix(customersPath) {
      pathPrefix(Segment) { segm =>
        get {
          complete{
            customersService.getCustomerById(segm).map[ToResponseMarshallable] {
              case result => result.statusCode -> result.payload
            }
          }
        } ~
        put {
          entity(as[NewCustomer]) { newCustomer =>
            complete {
              customersService.createCustomer(segm, newCustomer).map[ToResponseMarshallable] {
                case result => result.statusCode -> result.payload
              }
            }
          }
        }
      }
  } ~ getHello

  @ApiOperation(value = "Return Hello greeting", notes = "", nickname = "anonymousHello", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHello =
    path("hello") {
      get {
        complete { 200 -> "Hello, there" }
      }
    }
}
