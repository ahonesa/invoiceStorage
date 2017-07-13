package me.ahonesa.storage.db

import me.ahonesa.core.models.{Customer, NewCustomer}
import com.outworkers.phantom.dsl._
import me.ahonesa.storage.StorageConnector

import scala.concurrent.Future
import scala.util.{Failure, Success}

object CustomerStorage extends StorageConnector {

  abstract class CustomerTable extends Table[CustomerTable, Customer] {

    override def tableName: String = "customers"

    object customer_id extends StringColumn with PartitionKey

    object name extends StringColumn

    def findByCustomerId(id: String): Future[Option[Customer]] = {
      select
        .where(_.customer_id eqs id)
        .one()
    }

    def createCustomer(customerId: String, name: String) = {
      insert()
        .value(_.customer_id, customerId)
        .value(_.name, name).future()
    }
  }

  object table extends CustomerTable with connector.Connector

  def findByCustomerId(id: String): Future[Option[Customer]] = {
    table.findByCustomerId(id)
  }

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Option[Customer]] = {
    val customer = Customer(id, newCustomer.name.getOrElse(""))
    table.createCustomer(customer.id, customer.name).map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }
}