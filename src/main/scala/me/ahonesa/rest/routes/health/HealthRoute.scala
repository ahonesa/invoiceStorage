package me.ahonesa.rest.routes.health

import javax.ws.rs.Path

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import io.swagger.annotations._
import me.ahonesa.core.models.{Customer, NewCustomer}
import me.ahonesa.rest.services.CustomersService
import me.ahonesa.rest.utils.CommonJsonFormats

import scala.concurrent.ExecutionContext

@Path("/")
@Api(value = "/healh", produces = "application/json")
case class HealthRoute(implicit executionContext: ExecutionContext) extends CommonJsonFormats {

  val healthPath = "health"

  val route = pathPrefix(healthPath) {
    getHealth
  }

  @Path("/health")
  @ApiOperation(httpMethod = "GET", value = "Return Hello World")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Hello Greeting"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getHealth =
      get {
        complete { 200 -> "Hello, world" }
      }

}
