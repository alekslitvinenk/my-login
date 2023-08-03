/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.config

import io.dockovpn.dlogin.domain.User

case class LoginDockovpnConfig(
  storeType: String,
  bindInterface: String,
  toHttps: Boolean,
  sessionDurationSec: Long,
  resetExpirationSecThreshold: Long,
  expiredSessionsCheckIntervalSec: Option[Long],
  mapStorePredefinedUsers: Option[List[User]]
)
