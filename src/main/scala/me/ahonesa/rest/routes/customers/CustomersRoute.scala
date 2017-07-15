package me.ahonesa.rest.routes.customers

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models.{Customer, CustomerDetails}
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
      entity(as[CustomerDetails]) { newCustomer =>
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
  @(ApiModelProperty @field)(value = "customerNumber", required = false)
  customerNumber: Option[String] = None,
  @(ApiModelProperty @field)(value = "firstName", required = false)
  firstName: Option[String] = None,
  @(ApiModelProperty @field)(value = "lastName", required = false)
  lastName: Option[String] = None,
  @(ApiModelProperty @field)(value = "emailAddress", required = false)
  emailAddress: Option[String] = None,
  @(ApiModelProperty @field)(value = "phone", required = false)
  phone: Option[String] = None,
  @(ApiModelProperty @field)(value = "billingAddress1", required = false)
  billingAddress1: Option[String] = None,
  @(ApiModelProperty @field)(value = "billingAddress2", required = false)
  billingAddress2: Option[String] = None
)
