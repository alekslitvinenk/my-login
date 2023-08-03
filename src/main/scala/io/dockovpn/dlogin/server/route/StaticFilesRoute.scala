/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route

import akka.http.scaladsl.server.Directives.{get, getFromResourceDirectory}
import akka.http.scaladsl.server.Route

class StaticFilesRoute extends HasRoute {
  
  private val baseDir = sys.env.getOrElse("WWW_DIR", "web")
  
  override def route: Route = get {
    getFromResourceDirectory(baseDir)
  }
}

object StaticFilesRoute {
  def apply(): HasRoute = new StaticFilesRoute()
}


