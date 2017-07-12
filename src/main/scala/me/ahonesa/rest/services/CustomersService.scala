package me.ahonesa.rest.services

import me.ahonesa.storage.db.CustomerStorage
import me.ahonesa.core.models.{Customer, NewCustomer}

import scala.concurrent.{ExecutionContext, Future}

class CustomersService(implicit executionContext: ExecutionContext) {

  def getCustomerById(id: String): Future[Option[Customer]] = ???

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Option[Customer]] = ???


}