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

class CustomerServiceTest extends FunSuite with Matchers with MockFactory {

  trait TestData extends CommonJsonFormats {
    implicit val invoiceStorageMock = mock[InvoiceStorageAccess]
    val testCustomerId = "testCustomerId"
    val testCustomerDetails = CustomerDetails(firstName = Some("Matti"), lastName = Some("Näsä"))
    val testCustomer = Customer(testCustomerId, testCustomerDetails)

    val customerService = new CustomerService()
  }

  test("getCustomerById when customer exists with invoices") {
    new TestData {
      val invoice1 = Invoice("", "", LocalDate.of(2017,1,1), InvoiceSummary("1", 9.99, 0), Open, Set())
      val invoice2 = Invoice("", "", LocalDate.of(2017,2,1), InvoiceSummary("2", 0.99, 0.99), Closed,
        Set(InvoicePayment("0", LocalDate.of(2017,2,15), 0.99)))

      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))
      (invoiceStorageMock.findInvoicesByCustomerId _).expects(testCustomerId).returning(Future(List(invoice1, invoice2)))

      val result = Await.result(customerService.getCustomerById(testCustomerId), 5 seconds)
      result.statusCode shouldBe ResponseStatusCodes.OK
      result.payload.convertTo[CustomerWithInvoices].customer shouldBe testCustomer
      result.payload.convertTo[CustomerWithInvoices].invoices shouldBe List(invoice1, invoice2)
    }
  }

  test("getCustomerById when customer exists without invoices") {
    new TestData {
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))
      (invoiceStorageMock.findInvoicesByCustomerId _).expects(testCustomerId).returning(Future(List()))

      val result = Await.result(customerService.getCustomerById(testCustomerId), 5 seconds)
      result.statusCode shouldBe ResponseStatusCodes.OK
      result.payload.convertTo[CustomerWithInvoices].customer shouldBe testCustomer
      result.payload.convertTo[CustomerWithInvoices].invoices.isEmpty shouldBe true
    }
  }

  test("getCustomerById when customer does not exist") {
    new TestData {
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(None))

      val result = Await.result(customerService.getCustomerById(testCustomerId), 5 seconds)
      result.statusCode shouldBe ResponseStatusCodes.notFound
      result.payload.convertTo[String] shouldBe "customerId does not exist"
    }
  }

  test("getCustomerById with illegal customerId") {
    new TestData {
      val illegalId = "customer#1%"
      val result = Await.result(customerService.getCustomerById(illegalId), 5 seconds)
      result.statusCode shouldBe ResponseStatusCodes.validationError
      result.payload.convertTo[String] shouldBe "customerId validation error"
    }
  }

  test("createCustomer successfully") {
    new TestData {
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(None))
      (invoiceStorageMock.createCustomer _).expects(testCustomerId, testCustomerDetails).returning(Future(Some(testCustomer)))

      val result = Await.result(customerService.createCustomer(testCustomerId, testCustomerDetails), 5 seconds)

      result.statusCode shouldBe ResponseStatusCodes.OK
      result.payload.convertTo[Customer] shouldBe testCustomer
    }

  }

  test("createCustomer when customerId already exists") {
    new TestData {
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(Some(testCustomer)))

      val result = Await.result(customerService.createCustomer(testCustomerId, testCustomerDetails), 5 seconds)

      result.statusCode shouldBe ResponseStatusCodes.validationError
      result.payload.convertTo[String] shouldBe "customerId already exists"
    }

  }

  test("createCustomer with db returning None") {
    new TestData {
      (invoiceStorageMock.findCustomerById _).expects(testCustomerId).returning(Future(None))
      (invoiceStorageMock.createCustomer _).expects(testCustomerId, testCustomerDetails).returning(Future(None))

      val result = Await.result(customerService.createCustomer(testCustomerId, testCustomerDetails), 5 seconds)

      result.statusCode shouldBe ResponseStatusCodes.dbError
    }
  }
}
