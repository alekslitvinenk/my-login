/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.route

import akka.http.scaladsl.server.Route

trait HasRoute {
  
  def route: Route
}
