/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.service

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class UserServiceSpec extends AnyWordSpec {
  
  /*private val newValidUser = NewUser(
    "Name",
    "Surname",
    "user@example.com",
    "userName",
    "password",
  )*/
  
  "UserService::registerNewUser" should {
    
    "produce Either[None, RegistrationStatus] when newUser is valid" ignore {
      /*val service = new UserService()
      
      val expectedUser = User.tupled(NewUser.unapply(newValidUser).get :+ false)
      val expectedResult = Right(RegistrationStatus(
        expectedUser, RegistrationStatusCode.PendingEmailVerification
      ))
      val actualResult = service.registerNewUser(newValidUser)
      
      actualResult should be(expectedResult)*/
    }
    
    "produce RegistrationErrorCode.UserExists when user already registered" ignore {
      /*val service = new UserService()
      service.registerNewUser(newValidUser) should
        matchPattern { case Right(_) => }
      
      service.registerNewUser(newValidUser) should
        matchPattern { case Left(RegistrationError(_, RegistrationErrorCode.UserExists)) => }*/
    }
  }
}
