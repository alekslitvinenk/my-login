/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.config

import com.softwaremill.session.SessionConfig
import com.typesafe.config.Config
import pureconfig.ConfigSource.default
import pureconfig._
import pureconfig.generic.auto._

case class AppConfig(
  httpSession: SessionConfig,
  loginDockovpnConfig: LoginDockovpnConfig,
  slickConfig: Config
)

object AppConfig {
  
  private lazy val appConf = {
    val env = sys.env.getOrElse("DLOGIN_ENV", "dev")
    val confName = s"application-$env.conf"
    val source = default(ConfigSource.resources(confName))
    val sessionConfig: SessionConfig = source.config().map(SessionConfig.fromConfig).toOption.get
    
    val loginDockovpnConfig: LoginDockovpnConfig = source.config().map { v =>
      ConfigSource.fromConfig(v.getConfig("login-dockovpn")).loadOrThrow[LoginDockovpnConfig]
    }.toOption.get
    
    // FixMe: looks bit ugly
    val slickConfig: Config = source.config().map { v =>
      ConfigSource.fromConfig(v.getConfig("slick")).config()
    }.toOption.get.toOption.get
    
    new AppConfig(sessionConfig, loginDockovpnConfig, slickConfig)
  }
  
  def isDev: Boolean = sys.env.getOrElse("DLOGIN_ENV", "dev") == "dev"
  
  def apply(): AppConfig = appConf
}
