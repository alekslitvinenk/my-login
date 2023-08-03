/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route.api

import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.dockovpn.dlogin.domain.ProtocolFormat.JsonSupport
import io.dockovpn.dlogin.server.Directives._
import io.dockovpn.dlogin.server.route.HasRoute
import io.dockovpn.dlogin.service.{SessionService, UserService}

import scala.concurrent.ExecutionContext

class ApiRoute(userService: UserService, sessionService: SessionService)
              (implicit ec: ExecutionContext, mat: Materializer) extends HasRoute with JsonSupport {
  
  override def route: Route = {
    logRequestWithPayload(Logging.InfoLevel)(mat, ec) {
      preventCsrf {
        pathPrefix("api") {
          AuthApiRoute(userService, sessionService).route ~ DashboardApiRoute(userService, sessionService).route
        }
      }
    }
  }
}

object ApiRoute {
  def apply(userService: UserService, sessionService: SessionService)
           (implicit ec: ExecutionContext, mat: Materializer): HasRoute =
    new ApiRoute(userService, sessionService: SessionService)
}
