package me.ahonesa.rest.services

import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId}
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.InvoiceStorage
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class InvoicesService(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage) extends InvoicesValidator with CommonJsonFormats {

  def createInvoice(id: InvoiceId, newInvoice: NewInvoice): Future[Response] = {
    validateInvoiceId(id).flatMap( _.fold(
      left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
      right => invoiceStorage.createInvoice(id, newInvoice) ).map {
      case Some(res: Invoice) => Response( ResponseStatusCodes.OK, res.toJson )
      case None => Response( ResponseStatusCodes.dbError, JsNull )
    })
  }
}

trait InvoicesValidator extends CommonValidator {
  def validateInvoiceId(id: InvoiceId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findInvoiceById(id).map ( _ match {
        case Some(result) => Left("invoiceId already exists")
        case None => Right("ok")
      }
      )
      case false => Future(Left("invoiceId validation error"))
    }
  }
  // TODO: add validations for all other properties
}