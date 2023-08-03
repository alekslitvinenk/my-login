/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.dockovpn.dlogin.domain.Protocol.{LoginCredentials, NewUser, RegistrationInfo, RegistrationInfoStatuses}
import io.dockovpn.dlogin.domain.ProtocolFormat.JsonSupport
import io.dockovpn.dlogin.server.Directives._
import io.dockovpn.dlogin.service.ConversionService._
import io.dockovpn.dlogin.domain.RegistrationError
import io.dockovpn.dlogin.server.flow.AuthFlow
import io.dockovpn.dlogin.server.route.HasRoute
import io.dockovpn.dlogin.server.session.SessionKey
import io.dockovpn.dlogin.service.{SessionService, UserService}

import scala.concurrent.ExecutionContext

class AuthApiRoute(userService: UserService, sessionService: SessionService)
                  (implicit ec: ExecutionContext) extends HasRoute with JsonSupport {
  
  override def route: Route =
    path("do_login") {
      post {
        entity(as[LoginCredentials]) { loginCredentials =>
          val futureRes = new AuthFlow(userService, sessionService).run(loginCredentials)
        
          onSuccess(futureRes) {
            case Some(userSession) =>
              val sessionKey = SessionKey.composeSessionKey(userSession.userName, userSession.sessionId)
            
              setSessionKey(sessionKey) {
                startCsrf {
                  complete(StatusCodes.OK)
                }
              }
            case None => complete(StatusCodes.Unauthorized)
          }
        }
      }
    } ~ path("do_logout") {
      post {
        requireSessionKey { sessionKey =>
          requireUserSession(sessionKey)(sessionService) { userSession =>
            sessionService.closeSessionByLogout(userSession.sessionId) // fire and forget
            clearSessionKey { ctx =>
              ctx.complete(StatusCodes.OK)
            }
          }
        }
      }
    } ~ path("do_register") {
      post {
        entity(as[NewUser]) { newUser =>
          val futureRes = userService.registerNewUser(newUser)
        
          onSuccess(futureRes) {
            case Left(RegistrationError(_, errorCode)) =>
              complete(RegistrationInfo(RegistrationInfoStatuses.Error, Some(errorCode.toFieldError)))
            case Right(_) =>
              complete(RegistrationInfo(RegistrationInfoStatuses.Success, None))
          }
        }
      }
    }
}

object AuthApiRoute {
  def apply(userService: UserService, sessionService: SessionService)
           (implicit ec: ExecutionContext): HasRoute = new AuthApiRoute(userService, sessionService)
}
