/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.service

import io.dockovpn.metastore.store.AbstractStore
import io.dockovpn.metastore.util.FieldPredicate
import io.dockovpn.dlogin.domain.UserSessionState.{Active, Expired}
import io.dockovpn.dlogin.domain.{UserSession, UserSessionState}

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class SessionService(sessionStore: AbstractStore[UserSession],
                     sessionDuration: Long,
                     resetExpirationThreshold: Long)(implicit ec: ExecutionContext) {
  
  private val activeSessionsPredicate = FieldPredicate("state", "=", Active)
  
  def lookupSession(sessionId: String): Future[Option[UserSession]] = {
    sessionStore.get(sessionId)
  }
  
  def openSession(userName: String): Future[UserSession] = {
    
    val startTime = Instant.now()
    val expiresTime = startTime.plusSeconds(sessionDuration)
    
    val userSession = UserSession(
      sessionId = UUID.randomUUID().toString,
      userName = userName,
      state = UserSessionState.Active,
      timeCreated = Timestamp.from(startTime),
      lastTouched = Timestamp.from(startTime),
      timeExpires = Timestamp.from(expiresTime),
      timeClosed = None,
    )
    
    sessionStore.put(userSession.sessionId, userSession).map(_ => userSession)
  }
  
  def touchSession(sessionId: String): Future[Unit] = {
    sessionStore.get(sessionId).flatMap {
      case Some(userSession) if userSession.state == Active =>
        if (isAboutToExpire(userSession.timeExpires)) {
          resetTimeExpires(userSession)
        }
        Future.unit
      case _ => throw new IllegalStateException(s"User session with id [$sessionId] wasn't found")
    }
  }
  
  def touchSession(userSession: UserSession): Future[Unit] = {
    if (userSession.state == Active && isAboutToExpire(userSession.timeExpires)) {
      resetTimeExpires(userSession)
    }
    Future.unit
  }
  
  def closeSessionByLogout(sessionId: String): Future[Unit] =
    closeInternally(sessionId, UserSessionState.ClosedByUser)
    
  def closeSessionByApp(sessionId: String): Future[Unit] =
    closeInternally(sessionId, UserSessionState.ClosedByApp)
  
  def isNotExpired(userSession: UserSession): Boolean =
    Option(userSession.timeExpires) match {
      case None => false
      case Some(value) => Instant.now().isBefore(value.toInstant)
    }
  
  def isExpired(userSession: UserSession): Boolean = !isNotExpired(userSession)
  
  def markExpiredSessions(): Future[Unit] = {
    sessionStore.filter(activeSessionsPredicate).map { sessions =>
      sessions.filter(isExpired).foreach { userSession =>
        sessionStore.update(
          userSession.sessionId,
          userSession.copy(
            state = Expired, timeClosed = Some(Timestamp.from(Instant.now())))
        )
      }
    }
  }
    
  private def closeInternally(sessionId: String, closeType: String): Future[Unit] =
    sessionStore.get(sessionId).flatMap {
      case Some(session) =>
        sessionStore.update(
          sessionId,
          session.copy(
            state = closeType,
            timeClosed = Some(Timestamp.from(Instant.now()))
          )
        )
      case _ => throw new IllegalStateException(s"User session with id [$sessionId] wasn't found")
    }
  
  private def isAboutToExpire(timeExpires: Timestamp): Boolean =
    timeExpires.toInstant.getEpochSecond - Instant.now().getEpochSecond < resetExpirationThreshold
    
  private def resetTimeExpires(userSession: UserSession): Future[Unit] = {
    val lastTouched = Instant.now()
    val timeExpires = lastTouched.plusSeconds(sessionDuration)
    sessionStore.update(
      userSession.sessionId,
      userSession.copy(
        lastTouched = Timestamp.from(lastTouched),
        timeExpires = Timestamp.from(timeExpires)
      )
    )
  }
  
}

object SessionService {
  type SessionLookup = String => Future[Option[UserSession]]
}
