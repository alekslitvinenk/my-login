/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

import java.sql.Timestamp

object UserSessionState {
  val Active: String = "ACTIVE"
  val Expired: String = "EXPIRED"
  val ClosedByUser: String = "CLOSED"
  val ClosedByApp: String = "CLOSED_BY_APP"
}

case class UserSession(
  sessionId: String,
  userName: String,
  state: String,
  timeCreated: Timestamp,
  lastTouched: Timestamp,
  timeExpires: Timestamp,
  timeClosed: Option[Timestamp],
)
