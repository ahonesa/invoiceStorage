package me.ahonesa.rest.services

import java.time.LocalDate

import me.ahonesa.core.models._
import me.ahonesa.rest.utils.CommonJsonFormats
import me.ahonesa.storage.{InvoiceStorage, InvoiceStorageAccess}
import org.scalatest.{FunSuite, Matchers}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import spray.json._

class InvoicesServiceTest extends FunSuite with Matchers with MockFactory {

  trait TestData extends CommonJsonFormats {
    implicit val invoiceStorageMock = mock[InvoiceStorageAccess]
    val testCustomerId = "testCustomerId"
    val testCustomerDetails = CustomerDetails(firstName = Some("Matti"), lastName = Some("Näsä"))
    val testCustomer = Customer(testCustomerId, testCustomerDetails)
    val testInvoiceSummary = InvoiceSummary( "222", 99.95, 45.00 )
    val testNewInvoice = NewInvoice(testCustomerId, LocalDate.of(2017,1,1), testInvoiceSummary, PartiallyPaid )
    val testInvoiceId = "testInvoiceId"
    val testInvoice = Invoice(testInvoiceId, testCustomerId, testNewInvoice.invoiceDate, testInvoiceSummary, testNewInvoice.invoiceStatus, Set())

    val invoicesService = new InvoicesService()
  }

  test("createInvoice successfully") {
    new TestData {
      (invoiceStorageMock.findInvoiceById _).expects(testInvoiceId).returning(Future(None))
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))
      (invoiceStorageMock.createInvoice _).expects(testInvoiceId, testNewInvoice).returning(Future(Some(testInvoice)))

      val result = Await.result(invoicesService.createInvoice(testInvoiceId, testNewInvoice), 5 seconds)

      result.statusCode shouldBe ResponseStatusCodes.OK
      result.payload.convertTo[Invoice] shouldBe testInvoice
    }
  }
}
