package me.ahonesa.rest.utils

import java.util.UUID
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.ahonesa.core.models.{Customer, NewCustomer}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, deserializationError}

trait RestJsonFormats extends DefaultJsonProtocol with SprayJsonSupport {

  implicit object uuidFormat extends JsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(v) => UUID.fromString(v)
      case _           => deserializationError("UUID error")
    }

    override def write(uuid: UUID): JsValue = JsString(uuid.toString)
  }


  implicit val userEntityUpdateFormat = jsonFormat1(NewCustomer.apply)
  implicit val userEntityFormat = jsonFormat2(Customer.apply)
}
