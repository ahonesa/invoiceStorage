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

class InvoiceServiceTest extends FunSuite with Matchers with MockFactory {

  trait TestData extends CommonJsonFormats {
    implicit val invoiceStorageMock = mock[InvoiceStorageAccess]
    val testCustomerId = "testCustomerId"
    val testCustomerDetails = CustomerDetails(firstName = Some("Matti"), lastName = Some("Näsä"))
    val testCustomer = Customer(testCustomerId, testCustomerDetails)
    val testInvoiceSummary = InvoiceSummary( "222", 99.95, 0.00 )
    val testNewInvoice = NewInvoice(testCustomerId, LocalDate.of(2017,1,1), "222", 9.95 )
    val testInvoiceId = "testInvoiceId"
    val testInvoice = Invoice(testInvoiceId, testCustomerId, testNewInvoice.invoiceDate, testInvoiceSummary, Open, Set())

    val invoicesService = new InvoiceService()
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

  test("createPaymentForInvoice successfully") {
    new TestData {
      val testPayment = InvoicePayment("pay",LocalDate.of(2017,2,1), 45.00)
      val expectedInvoice = testInvoice.copy(
        invoiceSummary = InvoiceSummary( "222", 99.95, 45.00 ),
        invoicePayments = testInvoice.invoicePayments + testPayment,
        invoiceStatus = PartiallyPaid )

      (invoiceStorageMock.findInvoiceById _).expects(testInvoiceId).returning(Future(Some(testInvoice)))
      (invoiceStorageMock.updateInvoice _).expects(expectedInvoice).returning(Future(Some(expectedInvoice)))

      val result = Await.result(invoicesService.createPaymentForInvoice(testInvoiceId, testPayment), 5 seconds)

      result.statusCode shouldBe ResponseStatusCodes.OK
      result.payload.convertTo[Invoice] shouldBe expectedInvoice
    }
  }
}
