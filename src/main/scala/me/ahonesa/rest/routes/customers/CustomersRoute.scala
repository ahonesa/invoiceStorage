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
      get { getCustomer } ~ put { putCustomer }
  }

  @Path("/{customerId}")
  @ApiOperation(httpMethod = "GET", value = "get customer", produces = "text/plain")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "customerId", required = true, dataType = "string", paramType = "path", value = "Id of customer to be fetched")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return customer", response = classOf[Customer]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getCustomer =
      pathPrefix(Segment) { segm =>
        complete {
          customersService.getCustomerById(segm).map[ToResponseMarshallable] {
            case result => result.statusCode -> result.payload
          }
        }
      }

  @Path("/{customerId}")
  @ApiOperation(httpMethod = "PUT", value = "create customer", consumes = "application/json")
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
  def putCustomer =
    pathPrefix(Segment) { segm =>
      entity(as[NewCustomer]) { newCustomer =>
        complete {
          customersService.createCustomer(segm, newCustomer).map[ToResponseMarshallable] {
            case result => result.statusCode -> result.payload
          }
        }
      }
    }
}

@ApiModel(description = "A Customer object")
case class NewCustomerSwaggerModel(
 @(ApiModelProperty @field)(value = "name of the customer", required = false)
 name: Option[String] = None,
 @(ApiModelProperty @field)(value = "email of the customer", required = false)
 email: Option[String] = None
)