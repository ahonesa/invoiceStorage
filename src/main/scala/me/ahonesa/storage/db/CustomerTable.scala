package me.ahonesa.storage.db

import me.ahonesa.core.models.Customer
import com.outworkers.phantom.dsl._
import me.ahonesa.storage.StorageConnector

import scala.concurrent.Future

object CustomerStorage extends StorageConnector {

  abstract class CustomerTable extends Table[CustomerTable, Customer] {

    override def tableName: String = "customers"

    object customer_id extends StringColumn with PartitionKey

    object name extends StringColumn

    def findByCustomerId(id: String): Future[Option[Customer]] = {
      select
        .where(_.customer_id eqs id)
        .consistencyLevel_=(ConsistencyLevel.ONE)
        .one()
    }

  }

  object table extends CustomerTable with connector.Connector

  def findByCustomerId(id: String): Future[Option[Customer]] = {
    table.findByCustomerId(id)
  }
}