package me.ahonesa.core.models

case class Customer(customerId: String, customerDetails: CustomerDetails)

case class CustomerDetails(
  customerNumber: Option[String] = None,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  emailAddress: Option[String] = None,
  phone: Option[String] = None,
  billingAddress1: Option[String] = None,
  billingAddress2: Option[String] = None
)