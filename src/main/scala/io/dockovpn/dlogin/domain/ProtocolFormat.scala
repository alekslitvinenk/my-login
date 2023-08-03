/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import io.dockovpn.dlogin.domain.Protocol.{FieldError, LoginCredentials, NewUser, RegistrationInfo}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object ProtocolFormat {
  
  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val loginCredentialsFormat: RootJsonFormat[LoginCredentials] = jsonFormat2(LoginCredentials)
    implicit val newUserFormat: RootJsonFormat[NewUser] = jsonFormat5(NewUser)
    implicit val userFormat: RootJsonFormat[User] = jsonFormat8(User)
    implicit val fieldErrorFormat: RootJsonFormat[FieldError] = jsonFormat2(FieldError)
    implicit val registrationInfoFormat: RootJsonFormat[RegistrationInfo] = jsonFormat2(RegistrationInfo)
  }
}
