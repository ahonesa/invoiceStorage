package me.ahonesa.rest.utils

import java.time.LocalDate
import java.util.UUID

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

  implicit object localDateFormat extends JsonFormat[LocalDate] {
    def write(obj: LocalDate): JsValue = JsString(obj.toString)
    def read(json: JsValue): LocalDate = json match {
      case JsString(v) => LocalDate.parse(v)
      case _           => deserializationError("LocalDate deserializationError")
    }
  }

  implicit object invoiceStatusFormat extends JsonFormat[InvoiceStatus] {
    def write(obj: InvoiceStatus): JsValue = JsString(obj.toString)
    def read(json: JsValue): InvoiceStatus = json match {
      case JsString(v) => InvoiceStatus.fromString(v)
      case _           => deserializationError("InvoiceStatus deserializationError")
    }
  }

  implicit val userEntityUpdateFormat = jsonFormat1(NewCustomer.apply)
  implicit val userEntityFormat = jsonFormat2(Customer.apply)
  implicit val invoiceSummaryFormat = jsonFormat4(InvoiceSummary.apply)
  implicit val invoicePaymentFormat = jsonFormat3(InvoicePayment.apply)
}
