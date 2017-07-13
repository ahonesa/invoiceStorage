package me.ahonesa.core.models

case class Customer(id: String, name: String)

case class NewCustomer(name: Option[String] = None)