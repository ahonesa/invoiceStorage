package me.ahonesa.core.models

case class Response(statusCode: Int, payload: String )

object ResponseStatusCodes {
  val validationError = 422
  val dbError = 500
  val OK = 200
}
