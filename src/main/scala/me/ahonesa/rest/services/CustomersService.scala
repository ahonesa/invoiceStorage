package me.ahonesa.rest.services

import me.ahonesa.core.models.{Customer, NewCustomer, Response, ResponseStatusCodes}
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.Database
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class CustomersService(implicit executionContext: ExecutionContext) extends CustomerValidator with CommonJsonFormats {

  def getCustomerById(id: String): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
        right => Database.findByCustomerId(id) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }
    )
  }

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left.toJson)),
        right => Database.createCustomer(id, newCustomer) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson )
          case None => Response( ResponseStatusCodes.dbError, JsNull )
        }

    )
  }
}


trait CustomerValidator extends CommonValidator {

  def validateCustomerId(id: String)(implicit executionContext: ExecutionContext): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => Database.findByCustomerId(id).map ( _ match {
          case Some(result) => Left("customerId already exists")
          case None => Right("ok")
        }
      )
      case false => Future(Left("customerId validation error"))
    }
  }
}