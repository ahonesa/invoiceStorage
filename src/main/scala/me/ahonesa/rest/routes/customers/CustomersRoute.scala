package me.ahonesa.rest.routes.customers

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models.{Customer, NewCustomer}
import me.ahonesa.rest.services.CustomersService
import me.ahonesa.rest.utils.CommonJsonFormats
import io.swagger.annotations._
import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives

import scala.annotation.meta.field
import scala.concurrent.ExecutionContext

@Path("/customers")
@Api(value = "/customers")
case class CustomersRoute(customersService: CustomersService)(implicit executionContext: ExecutionContext) extends Directives with CommonJsonFormats {

  val customersPath = "customers"

  val route = pathPrefix(customersPath) {
    pathPrefix(Segment) { segm =>
      getCustomer(segm) ~ putCustomer(segm) }
  }

  @Path("/{customerId}")
  @ApiOperation(httpMethod = "GET", value = "get customer")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return customer", response = classOf[Customer]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getCustomer(segm: String) =
      get {
        complete {
          customersService.getCustomerById(segm).map[ToResponseMarshallable] {
            case result => result.statusCode -> result.payload
          }
        }
      }

  @Path("/{customerId}")
  @ApiOperation(httpMethod = "PUT", value = "create customer")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "customerId", required = true, dataType = "string", paramType = "path",
      value = "Id of customer to be created"),
    new ApiImplicitParam(name = "body", value = "Customer object to be created",
      dataType = "me.ahonesa.rest.routes.customers.NewCustomerSwaggerModel", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return customer"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def putCustomer(segm: String) =
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

case class NewCustomerSwaggerModel(@(ApiModelProperty @field)(required = false) name: Option[String] = None)