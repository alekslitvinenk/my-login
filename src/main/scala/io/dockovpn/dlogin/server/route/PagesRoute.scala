/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route

import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.dockovpn.dlogin.server.Directives._
import io.dockovpn.dlogin.service.SessionService

import scala.concurrent.ExecutionContext

class PagesRoute(sessionService: SessionService)(implicit ec: ExecutionContext, mat: Materializer) extends HasRoute {
  
  private val baseDir = sys.env.getOrElse("WWW_DIR", "web")
  
  override def route: Route = {
    logRequestWithPayload(Logging.InfoLevel)(mat, ec) {
      preventCsrf {
        pathSingleSlash {
          get {
            getFromResource(s"$baseDir/index.html")
          }
        } ~ path("dashboard") {
          get {
            requireSessionKey { session =>
              requireUserSession(session)(sessionService) { _ =>
                getFromResource(s"$baseDir/dashboard/index.html")
              }
            } ~ {
              redirect("/", StatusCodes.TemporaryRedirect)
            }
          }
        }
      }
    }
  }
}

object PagesRoute {
  def apply(sessionService: SessionService)
           (implicit ec: ExecutionContext, mat: Materializer): HasRoute = new PagesRoute(sessionService)
}
