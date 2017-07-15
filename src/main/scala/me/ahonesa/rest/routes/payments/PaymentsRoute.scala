package me.ahonesa.rest.routes.payments

import java.time.LocalDate

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import io.swagger.annotations._
import javax.ws.rs.Path
import akka.http.scaladsl.server.Directives
import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId}
import scala.annotation.meta.field
import scala.concurrent.ExecutionContext

@Path("/payments")
@Api(value = "/payments")
case class PaymentsRoute()(implicit executionContext: ExecutionContext) extends Directives with CommonJsonFormats {

  val paymentsPath = "payments"

  val route = pathPrefix(paymentsPath) {
    put { putPayment }
  }

  @Path("/{paymentId}/for/{invoiceId}")
  @ApiOperation(httpMethod = "PUT", value = "create payment for invoice", consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "paymentId", required = true, dataType = "string", paramType = "path",
      value = "Id of payment to be created"),
    new ApiImplicitParam(name = "invoiceId", required = true, dataType = "string", paramType = "path",
      value = "Id of invoice for the payment"),
    new ApiImplicitParam(name = "body", value = "Payment object to be created",
      dataType = "me.ahonesa.rest.routes.payments.NewPaymentSwaggerModel", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Payment"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def putPayment =
    pathPrefix(Segment) { paymentId =>
      pathPrefix("for") {
        pathPrefix(Segment) { invoiceId =>
          entity(as[NewPayment]) { newPayment =>
            complete {
              //      invoicesService.createInvoice(segm, newInvoice).map[ToResponseMarshallable] {
              //        case result => result.statusCode -> result.payload
              //      }
              200 -> "" // TODO: fix
            }
          }
        }
      }
    }
}

@ApiModel(value = "NewPayment")
case class NewPaymentSwaggerModel(
  @(ApiModelProperty @field)(value = "payment date", required = true)
  paymentDate: LocalDate,
  @(ApiModelProperty @field)(value = "payment amount", required = true)
  paymentAmount: BigDecimal
)