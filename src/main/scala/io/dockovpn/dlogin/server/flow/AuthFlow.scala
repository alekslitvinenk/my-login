/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.flow

import io.dockovpn.dlogin.domain.Protocol.LoginCredentials
import io.dockovpn.dlogin.domain.UserSessionState.Active
import io.dockovpn.dlogin.domain.UserSession
import io.dockovpn.dlogin.service.{SessionService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class AuthFlow(userService: UserService, sessionService: SessionService)(implicit ec: ExecutionContext) {
  
  def run(loginCredentials: LoginCredentials): Future[Option[UserSession]] = {
    userService.lookupUser(loginCredentials.userName).flatMap {
      case Some(user) if user.userPassword == loginCredentials.userPassword =>
        for {
          // check if user has previously opened session
          oldSessionOpt <- user.lastSession match {
            case Some(oldSessionId) => sessionService.lookupSession(oldSessionId)
            case None => Future.successful[Option[UserSession]](None)
          }
        
          // if user has previously opened session and its state is Active then close it
          _ <- oldSessionOpt match {
            case Some(oldSession) if oldSession.state == Active =>
              sessionService.closeSessionByApp(oldSession.sessionId)
            case _ => Future.unit
          }
        
          // open new session for user
          newSession <- sessionService.openSession(user.userName)
        
          // set user.lastSession to new session
          _ <- userService.updateUserSession(user.userName, newSession.sessionId)
        } yield Some(newSession)
    
      case _ => Future.successful(None)
    }
  }
  
}
