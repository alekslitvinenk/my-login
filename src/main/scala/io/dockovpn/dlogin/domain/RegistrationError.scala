/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

import RegistrationErrorCode.RegistrationErrorCode

object RegistrationErrorCode extends Enumeration {
  type RegistrationErrorCode = Value
  
  // userName errors
  val UserExists = Value
  val UserNameTooShort = Value
  val UserNameTooLong = Value
  val UserNameNotAllowedSymbols = Value
  
  // firstName errors
  val FirstNameTooShort = Value // if empty
  val FirstNameTooLong = Value // if more than 100 symbols
  val FirstNameNotAllowedSymbols = Value // only alphabetical symbols allowed
  
  // lastName errors
  val LastNameTooShort = Value // if less than 2 symbols
  val LastNameTooLong = Value // if more than 100 symbols
  val LastNameNotAllowedSymbols = Value // only alphabetical symbols allowed
  
  // email errors
  val EmailTooShort = Value // if less than 6 symbols i@g.me
  val EmailTooLong = Value // if more than 200 symbols
  val EmailNotValidFormat = Value // check with regex
  
  // userPassword errors
  val PasswordTooLong = Value
  val PasswordWeak = Value
}

case class RegistrationError(
  loginName: String,
  errorCode: RegistrationErrorCode
)
