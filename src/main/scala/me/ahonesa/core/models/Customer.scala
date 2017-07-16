package me.ahonesa.core.models

import me.ahonesa.core.models.identifiers.CustomerId

case class Customer(customerId: CustomerId, customerDetails: CustomerDetails)

case class CustomerDetails(
  customerNumber: Option[String] = None,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  emailAddress: Option[String] = None,
  phone: Option[String] = None,
  billingAddress1: Option[String] = None,
  billingAddress2: Option[String] = None
)

case class CustomerWithInvoices(
  customer: Customer,
  invoices: List[Invoice]
)