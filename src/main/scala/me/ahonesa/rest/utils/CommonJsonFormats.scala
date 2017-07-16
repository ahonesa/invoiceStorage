package me.ahonesa.rest.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.ahonesa.core.models._
import spray.json.DefaultJsonProtocol.{jsonFormat3, jsonFormat4}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, deserializationError}

trait CommonJsonFormats extends DefaultJsonProtocol with SprayJsonSupport {

  import java.time.format.DateTimeFormatter

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit object uuidFormat extends JsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(v) => UUID.fromString(v)
      case _           => deserializationError("UUID deserializationError")
    }

    override def write(uuid: UUID): JsValue = JsString(uuid.toString)
  }

  implicit object dateFormat extends JsonFormat[LocalDate] {
    def write(obj: LocalDate): JsValue = JsString(obj.toString)
    def read(json: JsValue): LocalDate = json match {
      case JsString(v) => LocalDate.parse(v, formatter)
      case _           => deserializationError("Date deserializationError")
    }
  }

  implicit object invoiceStatusFormat extends JsonFormat[InvoiceStatus] {
    def write(obj: InvoiceStatus): JsValue = JsString(obj.toString)
    def read(json: JsValue): InvoiceStatus = json match {
      case JsString(v) => InvoiceStatus.fromString(v)
      case _           => deserializationError("InvoiceStatus deserializationError")
    }
  }

  implicit val customerDetailsFormat = jsonFormat7(CustomerDetails.apply)
  implicit val userEntityFormat = jsonFormat2(Customer.apply)
  implicit val invoiceSummaryFormat = jsonFormat3(InvoiceSummary.apply)
  implicit val invoicePaymentFormat = jsonFormat3(InvoicePayment.apply)
  implicit val newInvoiceFormat = jsonFormat4(NewInvoice.apply)
  implicit val invoiceFormat = jsonFormat6(Invoice.apply)
  implicit val allCustomerDataFormat = jsonFormat2(CustomerWithInvoices.apply)
}
