/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.domain

object UserAuthMethod  {
  val LoginForm: String = "LOGIN_FORM"
  val SignInWithGoogle: String = "SIGN_IN_WITH_GOOGLE"
  val Facebook: String = ""
  val GitHub: String = ""
}

final case class User(
  firstName    : String,
  lastName     : String,
  email        : String,
  userName     : String,
  userPassword : String,
  emailVerified: Boolean = false,
  authMethod: String,
  lastSession: Option[String]
)
