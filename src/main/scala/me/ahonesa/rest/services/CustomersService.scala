package me.ahonesa.rest.services

import me.ahonesa.core.models.identifiers.CustomerId
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.InvoiceStorage
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class CustomersService(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage) extends CustomerValidator with CommonJsonFormats {

  def getCustomerById(id: CustomerId): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.notFound, left.toJson)),
        right => invoiceStorage.findCustomerById(id).flatMap {
          case Some(res: Customer) => invoiceStorage.findInvoicesByCustomerId(id).map { invoices =>
            Response(ResponseStatusCodes.OK, CustomerWithInvoices(res, invoices).toJson)
          }
          case None => Future(Response( ResponseStatusCodes.dbError, JsNull ))
        }
      )
    )
  }

  def createCustomer(id: CustomerId, customerDetails: CustomerDetails): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
        right => invoiceStorage.createCustomer(id, customerDetails).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }
      )
    )
  }
}


trait CustomerValidator extends CommonValidator {

  def validateCustomerId(id: CustomerId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findCustomerById(id).map ( _ match {
          case Some(result) => Right("OK")
          case None => Left("customerId does not exist")
        }
      )
      case false => Future(Left("customerId validation error"))
    }
  }


  // TODO: add validations for all customer properties
}