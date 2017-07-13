package me.ahonesa.rest.services

import me.ahonesa.core.models.{Response, ResponseStatusCodes}


trait CommonValidator {

  def containsNoSpecialChars(string: String) = string.matches("^[a-zA-Z0-9]*$")

  def wrapError(message: String, code: Int = ResponseStatusCodes.validationError) = Left(Response(code, message))
}
