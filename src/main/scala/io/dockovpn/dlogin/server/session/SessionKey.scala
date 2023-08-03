/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server.session

import com.softwaremill.session.{SessionSerializer, SingleValueSessionSerializer}

import scala.util.Try

case class SessionKey(value: String)

object SessionKey {
  
  def composeSessionKey(userName: String, sessionId: String): SessionKey =
    SessionKey(s"$userName~$sessionId")
    
  def decomposeSessionKey(sessionKey: SessionKey): (String, String) = {
    val components = sessionKey.value.split('~')
    (components(0), components(1))
  }
  
  implicit def serializer: SessionSerializer[SessionKey, String] =
    new SingleValueSessionSerializer(
      _.value,
      (un: String) =>
        Try {
          SessionKey(un)
        }
    )
}
