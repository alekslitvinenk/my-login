/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin

import akka.actor.ActorSystem
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import akka.http.scaladsl.Http
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import io.dockovpn.metastore.db.DBRef
import io.dockovpn.metastore.provider.StoreProvider
import io.dockovpn.metastore.store.AbstractStore
import io.dockovpn.dlogin.config.AppConfig
import io.dockovpn.dlogin.domain.ProtocolFormat.JsonSupport
import io.dockovpn.dlogin.domain.{User, UserSession}
import io.dockovpn.dlogin.server.route.MainRoute
import io.dockovpn.dlogin.service.provider.TableMetadataProvider
import io.dockovpn.dlogin.service.{SessionService, UserService}
import io.dockovpn.metastore.util.Lazy._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

object Main extends App with JsonSupport {
  
  private val env = sys.env.getOrElse("DLOGIN_ENV", "dev")
  // Make Akka read right config file
  sys.props.addOne("config.resource", s"application-$env.conf")
  
  implicit val system: ActorSystem = ActorSystem("dockovpn-actor-system")
  
  private val log = system.log
  
  if (!AppConfig.isDev) {
    // Akka Management hosts the HTTP routes used by bootstrap
    AkkaManagement(system).start()
  
    // Starting the bootstrap process needs to be done explicitly
    ClusterBootstrap(system).start()
  }
  implicit val executionContext: ExecutionContext = system.dispatcher
  
  private val appConfig = AppConfig()
  private val settings = appConfig.loginDockovpnConfig
  
  private val interface = settings.bindInterface
  
  private implicit val dbRef: DBRef = lazily { Database.forConfig("", appConfig.slickConfig) }
  private implicit val metadataProvider: TableMetadataProvider = new TableMetadataProvider()
  
  private val userStore: AbstractStore[User] = StoreProvider.getStoreByType(settings.storeType)
  private val userSessionStore: AbstractStore[UserSession] = StoreProvider.getStoreByType(settings.storeType)
  private val userService = new UserService(userStore)
  private val userSessionService = new SessionService(
    userSessionStore,
    settings.sessionDurationSec,
    settings.resetExpirationSecThreshold
  )
  
  private val mainRoute = MainRoute(userService, userSessionService).route
  
  private val bindingFuture = Http().newServerAt(interface, 8080).bind(mainRoute)
  
  if (!AppConfig.isDev) {
    ClusterSingleton(system.toTyped).init(
      SingletonActor(
        Behaviors.supervise(
          actor.SessionExpirationWatcher(settings, userSessionService)).onFailure[Exception](SupervisorStrategy.restart),
        "global-session-watcher"
      )
    )
  }
  
  // FixMe: Use coordinated shutdown task (only if necessary)
  sys.addShutdownHook {
    log.info("Shutting down")
    if (dbRef.isEvaluated) {
      dbRef.close()
    }
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete { _ =>
        log.info("Unbinding complete. shutting down akka system")
        system.terminate() // and shutdown when done
      }
  }
}
