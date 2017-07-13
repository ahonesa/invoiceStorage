package me.ahonesa.rest.services

import me.ahonesa.storage.db.CustomerStorage
import me.ahonesa.core.models.{Customer, NewCustomer, Response, ResponseStatusCodes}
import me.ahonesa.rest.utils.CommonJsonFormats
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class CustomersService(implicit executionContext: ExecutionContext) extends CustomerValidator with CommonJsonFormats {

  def getCustomerById(id: String): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left)),
        right => CustomerStorage.findByCustomerId(id) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson.prettyPrint )
          case None => Response( ResponseStatusCodes.dbError, "" )
        }
    )
  }

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Response] = {
    validateCustomerId(id).flatMap( _.fold(
        left => Future(Response(ResponseStatusCodes.validationError, left)),
        right => CustomerStorage.createCustomer(id, newCustomer) ).map {
          case Some(res: Customer) => Response( ResponseStatusCodes.OK, res.toJson.prettyPrint )
          case None => Response( ResponseStatusCodes.dbError, "" )
        }

    )
  }
}


trait CustomerValidator extends CommonValidator {

  def validateCustomerId(id: String)(implicit executionContext: ExecutionContext): Future[Either[String, String]] = {
    containsNoSpecialChars(id) match {
      case true => CustomerStorage.findByCustomerId(id).map ( _ match {
          case Some(result) => Left("customerId already exists")
          case None => Right("ok")
        }
      )
      case false => Future(Left("customerId validation error"))
    }
  }
}