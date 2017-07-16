package me.ahonesa.core.models

import spray.json.JsValue

case class Response(statusCode: Int, payload: JsValue )

object ResponseStatusCodes {
  val validationError = 422
  val notFound = 404
  val dbError = 500
  val OK = 200
}
