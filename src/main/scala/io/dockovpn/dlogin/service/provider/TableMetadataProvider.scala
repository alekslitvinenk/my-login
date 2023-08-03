/*
 * Copyright (c) 2023. Dockovpn Solutions. All Rights Reserved
 */

package io.dockovpn.dlogin.service.provider

import io.dockovpn.metastore.provider.{AbstractTableMetadataProvider, TableMetadata}
import io.dockovpn.metastore.util.Types.getType
import io.dockovpn.dlogin.domain.{User, UserSession}
import slick.jdbc.GetResult

import scala.reflect.ClassTag

class TableMetadataProvider extends AbstractTableMetadataProvider {
  
  def getTableMetadata[V](implicit tag: ClassTag[V]): TableMetadata = {
    val UserClass: String = getType[User].toString
    val UserSessionClass: String = getType[UserSession].toString
    
    tag.runtimeClass.getName match {
      case UserClass =>
        new TableMetadata(
          tableName = "users",
          fieldName = "user_name",
          rconv = GetResult { r =>
            r.skip
            User(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<)
          }
        )
      case UserSessionClass =>
        new TableMetadata(
          tableName = "user_sessions",
          fieldName = "session_id",
          rconv = GetResult { r =>
            r.skip
            UserSession(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<)
          }
        )
      case unrecognizedType =>
        throw new IllegalArgumentException(s"Unrecognized type [$unrecognizedType] to get TableMetadata for")
    }
  }
}
