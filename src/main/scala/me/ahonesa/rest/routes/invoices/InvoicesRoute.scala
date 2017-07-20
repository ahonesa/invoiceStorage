package me.ahonesa.rest.routes.invoices

import java.time.LocalDate

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import io.swagger.annotations._
import javax.ws.rs.Path
import akka.http.scaladsl.server.Directives
import me.ahonesa.core.models.identifiers.CustomerId
import me.ahonesa.rest.services.InvoiceService
import scala.annotation.meta.field
import scala.concurrent.ExecutionContext

@Path("/invoices")
@Api(value = "/invoices")
case class InvoicesRoute(invoicesService: InvoiceService)(implicit executionContext: ExecutionContext) extends Directives with CommonJsonFormats {

  val invoicesPath = "invoices"

  val route = pathPrefix(invoicesPath) {
    put { putInvoice }
  }

  @Path("/{invoiceId}")
  @ApiOperation(httpMethod = "PUT", value = "create invoice", consumes = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "invoiceId", required = true, dataType = "string", paramType = "path",
      value = "Id of invoice to be created"),
    new ApiImplicitParam(name = "body", value = "Invoice object to be created",
      dataType = "me.ahonesa.rest.routes.invoices.NewInvoiceSwaggerModel", required = true, paramType = "body")
  ))
  def putInvoice =
    pathPrefix(Segment) { segm =>
      entity(as[NewInvoice]) { newInvoice =>
        complete {
          invoicesService.createInvoice(segm, newInvoice).map[ToResponseMarshallable] {
            case result => result.statusCode -> result.payload
          }
        }
      }
    }
}

@ApiModel(value = "NewInvoice")
case class NewInvoiceSwaggerModel(
  @(ApiModelProperty @field)(value = "id of the customer", required = true)
  customerId: CustomerId,
  @(ApiModelProperty @field)(value = "invoice date", required = true)
  invoiceDate: LocalDate,
  @(ApiModelProperty @field)(value = "invoice number", required = true)
  invoiceNumber: String,
  @(ApiModelProperty @field)(value = "toBePaid", required = true )
  toBePaid: BigDecimal
)
