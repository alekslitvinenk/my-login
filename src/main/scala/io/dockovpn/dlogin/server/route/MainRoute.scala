/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._enhanceRouteWithConcatenation
import akka.stream.Materializer
import io.dockovpn.dlogin.server.Directives.{preventEmbedding, toHttps, trimTrailingSlash}
import io.dockovpn.dlogin.service.{SessionService, UserService}

import scala.concurrent.ExecutionContext

class MainRoute(userService: UserService, sessionService: SessionService)
               (implicit ec: ExecutionContext, mat: Materializer) extends HasRoute {
  private val baseRoute = PagesRoute(sessionService).route ~ api.ApiRoute(userService, sessionService).route
  private val staticFilesRoute = StaticFilesRoute().route
  private val allRoute = toHttps(baseRoute) ~ staticFilesRoute
  private val unifiedRoute = trimTrailingSlash(allRoute)
  private val armoredRoute = preventEmbedding(unifiedRoute)
  
  override def route: Route = armoredRoute
}

object MainRoute {
  def apply(userService: UserService, sessionService: SessionService)
           (implicit ec: ExecutionContext, mat: Materializer) = new MainRoute(userService, sessionService)
}
