package me.ahonesa.core.models

import java.util.UUID

case class Customer(id: String, name: String)

case class NewCustomer(name: Option[String] = None)