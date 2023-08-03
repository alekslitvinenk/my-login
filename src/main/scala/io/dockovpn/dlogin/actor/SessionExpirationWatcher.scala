/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.dockovpn.dlogin.config.LoginDockovpnConfig
import io.dockovpn.dlogin.service.SessionService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

object SessionExpirationWatcher {
  
  sealed trait Command
  case object MarkAllExpiredSessions extends Command
  
  def apply(settings: LoginDockovpnConfig, sessionService: SessionService): Behavior[Command] =
    Behaviors.setup { ctx =>
      val log = ctx.log
      
      if (settings.expiredSessionsCheckIntervalSec.isDefined) {
        implicit val ec: ExecutionContext = ctx.system.executionContext
        
        val runnable = new Runnable {
          override def run(): Unit = ctx.self ! MarkAllExpiredSessions
        }
        
        val interval = settings.expiredSessionsCheckIntervalSec.get.seconds
    
        ctx.system.scheduler.scheduleWithFixedDelay(
          interval,
          interval,
        )(runnable)
    
        Behaviors.receiveMessage {
          case MarkAllExpiredSessions =>
            log.info("MarkAllExpiredSessions message received")
            sessionService.markExpiredSessions()
            Behaviors.same
          case x =>
            log.warn(s"Unrecognized message received [$x]")
            Behaviors.same
        }
      } else Behaviors.stopped
    }
  
}
