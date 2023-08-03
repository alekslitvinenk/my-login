/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

import RegistrationStatusCode.RegistrationStatusCode

object RegistrationStatusCode extends Enumeration {
  type RegistrationStatusCode = Value
  
  val PendingEmailVerification, EmailVerified = Value
}

case class RegistrationStatus(
  user: User,
  status: RegistrationStatusCode
)
