/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.server

import akka.event.Logging.LogLevel
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.Uri.Path.Slash
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RouteResult.Complete
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LogEntry, LoggingMagnet}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import io.dockovpn.dlogin.domain.UserSessionState._
import com.softwaremill.session.CsrfDirectives.{hmacTokenCsrfProtection, setNewCsrfToken}
import com.softwaremill.session.CsrfOptions.checkHeader
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions.{oneOff, usingCookies}
import com.softwaremill.session.{SessionConfig, SessionManager}
import io.dockovpn.dlogin.config.AppConfig
import io.dockovpn.dlogin.domain.UserSession
import io.dockovpn.dlogin.server.session.SessionKey
import io.dockovpn.dlogin.service.SessionService

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object Directives {
  
  private val settings = AppConfig().loginDockovpnConfig
  private val redirectToHTTPS: Boolean = settings.toHttps
  
  private val sessionConfig: SessionConfig = AppConfig().httpSession
  private implicit val sessionManager: SessionManager[SessionKey] = new SessionManager[SessionKey](sessionConfig)
  
  val trimTrailingSlash: Directive0 = extractUnmatchedPath.flatMap {
    case fullPath @ Slash(subPath) if subPath.endsWithSlash =>
      redirect(fullPath.toString().dropRight(1), StatusCodes.TemporaryRedirect)
    case _ => pass
  }
  
  val toHttps: Directive0 = extractScheme.flatMap {
    case "http" if redirectToHTTPS =>
      extractUri.flatMap { uri =>
        val httpsUri = uri.copy(scheme = "https")
        redirect(httpsUri, StatusCodes.PermanentRedirect)
      }
    case _ => pass
  }
  
  val preventEmbedding: Directive0 = respondWithHeader(RawHeader("X-Frame-Options", "DENY"))
  
  val requireSessionKey: Directive1[SessionKey] = requiredSession(oneOff, usingCookies)
  val clearSessionKey: Directive0 = invalidateSession(oneOff, usingCookies)
  val touchRequiredSessionKey: Directive0 = touchRequiredSession(oneOff, usingCookies).flatMap(_ => pass)
  
  def setSessionKey(v: SessionKey): Directive0 = setSession(oneOff, usingCookies, v)
  
  val preventCsrf: Directive0 = hmacTokenCsrfProtection(checkHeader)
  val startCsrf: Directive0 = setNewCsrfToken(checkHeader)
  
  def requireUserSession(sessionKey: SessionKey)
                        (implicit sessionService: SessionService): Directive1[UserSession] = {
    val (userName, sessionId) = SessionKey.decomposeSessionKey(sessionKey)
  
    onComplete(sessionService.lookupSession(sessionId)) flatMap {
      case Success(Some(userSession)) if userSession.userName == userName && userSession.state == Active =>
        if (sessionService.isNotExpired(userSession))
          provide(userSession)
        else reject(AuthorizationFailedRejection)
      case _ => reject(AuthorizationFailedRejection)
    }
  }
  
  def requireUserSessionAndTouchIt(sessionKey: SessionKey)
                                (implicit sessionService: SessionService): Directive1[UserSession] = {
    requireUserSession(sessionKey).flatMap { userSession =>
      sessionService.touchSession(userSession) // fire and forget
      provide(userSession)
    }
  }
  
  def logRequestWithPayload(level: LogLevel)(implicit m: Materializer, ex: ExecutionContext): Directive0 = {
    def loggingFunction(logger: LoggingAdapter)(req: HttpRequest)(res: Any): Unit = {
      val entry = res match {
        case Complete(resp) =>
          for {
            reqEntity <- entityAsString(req.entity)
            respEntity <- resp.entity.contentLengthOption match {
              case Some(len) if len != 0 => entityAsString(resp.entity)
              case _ => Future.successful("")
            }
          } yield LogEntry(s"${req.method.value} ${req.uri.path} \n Request entity: $reqEntity \n Response status: ${resp.status} \n Response entity: $respEntity", level)
        case other => Future.successful(LogEntry(s"$other", Logging.DebugLevel))
      }
      entry.foreach(_.logTo(logger))
    }
  
    DebuggingDirectives.logRequestResult(LoggingMagnet(log => loggingFunction(log))).tflatMap(_ => pass)
  }
  
  private def entityAsString(entity: HttpEntity)
                    (implicit m: Materializer): Future[String] = {
    entity.dataBytes
      .map(_.decodeString(entity.contentType.charsetOption.get.value))
      .runWith(Sink.head)
  }
}
