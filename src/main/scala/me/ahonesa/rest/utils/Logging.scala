package me.ahonesa.rest.utils

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  val logger = LoggerFactory.getLogger(getClass)
}
