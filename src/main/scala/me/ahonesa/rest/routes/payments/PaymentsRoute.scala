package me.ahonesa.rest.routes.payments

import java.time.LocalDate

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import io.swagger.annotations._
import javax.ws.rs.Path

import akka.http.scaladsl.server.Directives
import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId, PaymentId}
import me.ahonesa.rest.services.InvoicesService

import scala.annotation.meta.field
import scala.concurrent.ExecutionContext

@Path("/payments")
@Api(value = "/payments")
case class PaymentsRoute(invoicesService: InvoicesService)(implicit executionContext: ExecutionContext) extends Directives with CommonJsonFormats {

  val paymentsPath = "payments"

  val route = pathPrefix(paymentsPath) {
    post { putPayment }
  }

  @Path("/for/{invoiceId}")
  @ApiOperation(httpMethod = "POST", value = "create payment for invoice", consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "invoiceId", required = true, dataType = "string", paramType = "path",
      value = "Id of invoice for the payment"),
    new ApiImplicitParam(name = "body", value = "Payment object to be created",
      dataType = "me.ahonesa.rest.routes.payments.NewPaymentSwaggerModel", required = true, paramType = "body")
  ))
  def putPayment = pathPrefix("for" / Segment) { invoiceId =>
    entity(as[InvoicePayment]) { newPayment =>
      complete {
        invoicesService.createPaymentForInvoice(invoiceId, newPayment).map[ToResponseMarshallable] {
          case result => result.statusCode -> result.payload
        }
      }
    }
  }
}

@ApiModel(value = "NewPayment")
case class NewPaymentSwaggerModel(
  @(ApiModelProperty @field)(value = "paymentId", required = true)
  paymentId: PaymentId,
  @(ApiModelProperty @field)(value = "payment date", required = true)
  paymentDate: LocalDate,
  @(ApiModelProperty @field)(value = "payment amount", required = true)
  paymentAmount: BigDecimal
)
