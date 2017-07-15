package me.ahonesa.storage.db

import me.ahonesa.core.models.{Customer, CustomerDetails}
import com.outworkers.phantom.dsl._
import me.ahonesa.core.models.identifiers.CustomerId

import scala.concurrent.Future
import me.ahonesa.storage._

abstract class CustomerTable extends Table[CustomerTable, Customer] {

  override def tableName: String = "customers"

  object customerId extends StringColumn with PartitionKey

  object customerDetails extends JsonColumn[CustomerDetails]

  def findByCustomerId(customerId: CustomerId): Future[Option[Customer]] = {
    select
      .where(_.customerId eqs customerId)
      .one()
  }
}




