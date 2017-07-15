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
        left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
        right => invoiceStorage.findCustomerById(id) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }
    )
  }

  def createCustomer(id: CustomerId, customerDetails: CustomerDetails): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
        right => invoiceStorage.createCustomer(id, customerDetails) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }

    )
  }
}


trait CustomerValidator extends CommonValidator {

  def validateCustomerId(id: CustomerId)(implicit executionContext: ExecutionContext, invoiceStorage: InvoiceStorage): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => invoiceStorage.findCustomerById(id).map ( _ match {
          case Some(result) => Left("customerId already exists")
          case None => Right("ok")
        }
      )
      case false => Future(Left("customerId validation error"))
    }
  }


  // TODO: add validations for all customer properties
}