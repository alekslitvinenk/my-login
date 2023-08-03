/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import io.dockovpn.dlogin.server.Directives.{requireSessionKey, requireUserSessionAndTouchIt, touchRequiredSessionKey}
import io.dockovpn.dlogin.server.route.HasRoute
import io.dockovpn.dlogin.service.{SessionService, UserService}

class DashboardApiRoute(userService: UserService, sessionService: SessionService) extends HasRoute {
  
  override def route: Route =
    requireSessionKey { sessionKey =>
      requireUserSessionAndTouchIt(sessionKey)(sessionService) { _ =>
        touchRequiredSessionKey {
          path("current_login") {
            get {
              complete(StatusCodes.OK)
            }
          }
        }
      }
    }
}

object DashboardApiRoute {
  def apply(userService: UserService, sessionService: SessionService): HasRoute =
    new DashboardApiRoute(userService, sessionService)
}
