package me.ahonesa.rest.services

import me.ahonesa.core.models.identifiers.CustomerId
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.{InvoiceStorage, InvoiceStorageAccess}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class CustomerService(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorageAccess) extends CustomerValidator with CommonJsonFormats {

  def getCustomerById(id: CustomerId): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(left),
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
    validateCustomerIdForCreate(id).flatMap( _.fold(
        left => Future(left),
        right => invoiceStorage.createCustomer(id, customerDetails).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }
      )
    )
  }
}


trait CustomerValidator extends CommonValidator with CommonJsonFormats {

  def validateCustomerId(id: CustomerId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorageAccess): Future[Either[Response, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findCustomerById(id).map ( _ match {
          case Some(result) => Right("OK")
          case None => Left(Response(ResponseStatusCodes.notFound, "customerId does not exist".toJson))
        }
      )
      case false => Future(Left(Response(ResponseStatusCodes.validationError ,"customerId validation error".toJson)))
    }
  }

  def validateCustomerIdForCreate(id: CustomerId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorageAccess): Future[Either[Response, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findCustomerById(id).map ( _ match {
        case Some(result) => Left(Response(ResponseStatusCodes.validationError, "customerId already exists".toJson))
        case None => Right("OK")
      }
      )
      case false => Future(Left(Response(ResponseStatusCodes.validationError ,"customerId validation error".toJson)))
    }
  }


  // TODO: add validations for all customer properties
}