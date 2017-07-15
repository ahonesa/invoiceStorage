package me.ahonesa.rest.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.ahonesa.core.models._
import spray.json.DefaultJsonProtocol.{jsonFormat3, jsonFormat4}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, deserializationError}

trait CommonJsonFormats extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object uuidFormat extends JsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(v) => UUID.fromString(v)
      case _           => deserializationError("UUID deserializationError")
    }

    override def write(uuid: UUID): JsValue = JsString(uuid.toString)
  }

  implicit object dateFormat extends JsonFormat[Date] {
    def write(obj: Date): JsValue = JsString(obj.toString)
    def read(json: JsValue): Date = json match {
      case JsString(v) => new SimpleDateFormat("yyyy-MM-dd").parse(v)
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
  implicit val invoicePaymentFormat = jsonFormat4(InvoicePayment.apply)
  implicit val newInvoiceFormat = jsonFormat4(NewInvoice.apply)
  implicit val newPaymentFormat = jsonFormat2(NewPayment.apply)
  implicit val invoiceFormat = jsonFormat6(Invoice.apply)
}
