package me.ahonesa.storage

import java.util.concurrent.TimeUnit

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints, KeySpace}
import com.outworkers.phantom.database.Database
import me.ahonesa.Main
import me.ahonesa.core.models.{Customer, NewCustomer}
import me.ahonesa.rest.utils.Config
import me.ahonesa.storage.db.CustomerTable

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import com.outworkers.phantom.dsl._

import scala.util.{Failure, Success, Try}

object StorageConnector extends Config {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
      .withClusterBuilder(_.withCredentials(username, password))
      .keySpace(keyspaceName)

  implicit val session = Try(connector.session) match {
    case Success(session) =>
      println("Connected to Cassandra")
      session
    case Failure(ex) => {
      println("Couldn't connect to Cassandra", ex)
      sys.exit(1)
    }
  }
}

class InvoiceStorage(override val connector: CassandraConnection)(executionContext: ExecutionContext)
  extends Database[InvoiceStorage](connector) with Config {

  object CustomerTable extends CustomerTable with connector.Connector

  implicit val keySpace = KeySpace(keyspaceName)

  val createFuture = Await.ready(CustomerTable.create.ifNotExists().future(), 3.seconds)

  def findByCustomerId(id: String): Future[Option[Customer]] = {
    CustomerTable.findByCustomerId(id)
  }

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Option[Customer]] = {
    val customer = Customer(id, newCustomer.name.getOrElse(""))
    CustomerTable.createCustomer(customer.id, customer.name).map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }
}


