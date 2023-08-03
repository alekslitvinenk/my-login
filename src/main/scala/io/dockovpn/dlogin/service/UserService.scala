/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.service

import io.dockovpn.metastore.store.AbstractStore
import io.dockovpn.dlogin.domain.Protocol.NewUser
import io.dockovpn.dlogin.domain._

import scala.concurrent.{ExecutionContext, Future}

class UserService(userStore: AbstractStore[User])(implicit ec: ExecutionContext) {
  
  def lookupUser(loginName: String): Future[Option[User]] =
    userStore.get(loginName).map { userOpt =>
      userOpt.flatMap { user =>
        if (user.emailVerified && user.authMethod == UserAuthMethod.LoginForm) Some(user)
        else None
      }
    }
  
  def updateUserSession(userName: String, sessionId: String): Future[Unit] =
    userStore.get(userName).flatMap {
      case Some(user) => userStore.update(user.userName, user.copy(lastSession = Some(sessionId)))
      case None => Future.unit
    }
  
  def registerNewUser(newUser: NewUser): Future[Either[RegistrationError, RegistrationStatus]] = {
    userStore.contains(newUser.userName).map { userExists =>
      if (userExists)
        Left(
          RegistrationError(
            newUser.userName,
            RegistrationErrorCode.UserExists)
        )
      else {
        validateUser(newUser).fold[Either[RegistrationError, RegistrationStatus]] {
          val user = User(
            firstName = newUser.firstName,
            lastName = newUser.lastName,
            email = newUser.email,
            userName = newUser.userName,
            userPassword = newUser.userPassword,
            emailVerified = newUser.userName == "alekslitvinenk",
            authMethod = UserAuthMethod.LoginForm,
            lastSession = None
          )
      
          userStore.put(newUser.userName, user)
      
          Right(
            RegistrationStatus(
              user = user,
              status = if (user.emailVerified) RegistrationStatusCode.EmailVerified else RegistrationStatusCode.PendingEmailVerification
            )
          )
        } { regErr =>
          Left(regErr)
        }
      }
    }
  }
  
  private def validateUser(newUser: NewUser): Option[RegistrationError] = {
    identity(newUser) // to bypass unused arg warning
    None
  }
}
