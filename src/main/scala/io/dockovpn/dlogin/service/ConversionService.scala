/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.service

import io.dockovpn.dlogin.domain.Protocol.FieldError
import io.dockovpn.dlogin.domain.RegistrationErrorCode.{UserExists, PasswordWeak}

object ConversionService {
  
  import io.dockovpn.dlogin.domain.RegistrationErrorCode.RegistrationErrorCode
  
  implicit class RegistrationErrorCodeConverter(errorCode: RegistrationErrorCode) {
    
    def toFieldError: FieldError = {
      errorCode match {
        case UserExists => FieldError("userName", "User with this name already exists within the system.")
        case PasswordWeak => FieldError("userPassword", "Password is too weak.")
      }
    }
  }
}
