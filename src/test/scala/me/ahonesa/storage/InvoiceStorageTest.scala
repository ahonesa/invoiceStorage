package me.ahonesa.storage

import me.ahonesa.core.models.CustomerDetails
import org.scalatest._
import scala.concurrent.Await
import scala.concurrent.duration._

class InvoiceStorageTest extends CassandraSpec with Matchers with BeforeAndAfterAll {

  "Customer" should {
    "be inserted to Cassandra" in {
      Await.result(database.createCustomer("testCustomer", CustomerDetails()), 5 seconds)
      val result = Await.result(database.findCustomerById("testCustomer"), 5 seconds)
      result.isDefined shouldBe true
    }
  }

}
