package me.ahonesa.rest.services

import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId, PaymentId}
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.InvoiceStorage
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class InvoicesService(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage) extends InvoicesValidator with CommonJsonFormats with CommonValidator {

  def createInvoice(id: InvoiceId, newInvoice: NewInvoice): Future[Response] = {
    validateInvoiceId(id).flatMap( _.fold(
      left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
      right => {
        validateCustomerIdExists(newInvoice.customerId).flatMap( _.fold(
          left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
          right => invoiceStorage.createInvoice(id, newInvoice) ).map {
          case Some(res: Invoice) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        })
      })
    )
  }

  def createPaymentForInvoice(invoiceId: InvoiceId, newPayment: InvoicePayment): Future[Response] = {
    invoiceStorage.findInvoiceById(invoiceId).flatMap( _ match {
      case Some(invoice) => invoiceStorage.updateInvoice(updateInvoiceWithPayment(invoice, newPayment)).map {
        case Some(res: Invoice) => Response( ResponseStatusCodes.OK, res.toJson )
        case None => Response( ResponseStatusCodes.dbError, JsNull )
      }
      case None => Future(Response(ResponseStatusCodes.notFound, "invoiceId does not exist".toJson))
    })
  }

  private def updateInvoiceWithPayment(invoice: Invoice, newPayment: InvoicePayment): Invoice = {
    val payments = invoice.invoicePayments + newPayment
    val updatedAlreadyPaid = payments.toList.map(_.paymentAmount).sum
    val updatedSummary = invoice.invoiceSummary.copy(alreadyPaid = updatedAlreadyPaid)
    val updatedStatus = updateInvoiceStatus(updatedSummary)
    invoice.copy(invoiceStatus = updatedStatus, invoiceSummary = updatedSummary, invoicePayments = payments)
  }

  private def updateInvoiceStatus(invoiceSummary: InvoiceSummary) =
    if(invoiceSummary.alreadyPaid >= invoiceSummary.toBePaid) Closed
    else if(invoiceSummary.alreadyPaid > 0) PartiallyPaid
    else Open
}

trait InvoicesValidator extends CommonValidator {
  def validateInvoiceId(id: InvoiceId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findInvoiceById(id).map ( _ match {
        case Some(result) => Left("invoiceId already exists")
        case None => Right("ok")
      })
      case false => Future(Left("invoiceId validation error"))
    }
  }

  def validateCustomerIdExists(id: CustomerId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findCustomerById(id).map ( _ match {
        case Some(result) => Right("ok")
        case None => Left("customerId does not exist")
      })
      case false => Future(Left("customerId validation error"))
    }
  }
  // TODO: add validations for all other properties
}