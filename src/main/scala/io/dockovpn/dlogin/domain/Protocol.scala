/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

object Protocol {
  
  case class LoginCredentials(userName: String, userPassword: String)
  
  case class NewUser(
    firstName   : String,
    lastName    : String,
    email       : String,
    userName    : String,
    userPassword: String,
  )
  
  case class FieldError(field: String, error: String)
  
  object RegistrationInfoStatuses {
    val Success = "success"
    val Error = "error"
    val Values: List[String] = List(Success, Error)
  }
  
  case class RegistrationInfo(
    status: String,
    errors: Option[FieldError]
  ) {
    require(RegistrationInfoStatuses.Values.contains(status), "'status' field should be either 'success' or 'error'")
  }
}
