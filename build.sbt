import sbt.Keys.libraryDependencies

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / assemblyMergeStrategy := {
    case PathList(ps @ _*) if ps.last == "module-info.class" =>
        MergeStrategy.discard
    case x =>
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "login.dockovpn.io",
    scalacOptions ++= Seq(
        "-deprecation",
        "-encoding", "utf-8",                // Specify character encoding used by source files.
        "-explaintypes",                     // Explain type errors in more detail.
        "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
        "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
        "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
        "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
        "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
        "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
        "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
        "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
        "-Ywarn-dead-code",                  // Warn when dead code is identified.
        "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
        "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
        "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
        //"-Ywarn-unused:locals",              // Warn if a local definition is unused.
        //"-Ywarn-unused:params",              // Warn if a value parameter is unused.
        "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
        "-Ywarn-unused:privates",            // Warn if a private member is unused.
        //"-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
    ),
    libraryDependencies ++= Seq(
        scalaOrganization.value % "scala-reflect" % scalaVersion.value,
        "io.dockovpn" %% "metastore" % "0.1.0-SNAPSHOT",
        "com.typesafe.akka" %% "akka-http" % "10.4.0",
        "com.typesafe.akka" %% "akka-http-spray-json" % "10.4.0",
        "com.typesafe.akka" %% "akka-stream" % "2.7.0",
        "com.typesafe.akka" %% "akka-actor-typed" % "2.7.0",
        "com.typesafe.akka" %% "akka-cluster-typed" % "2.7.0",
        "com.typesafe.slick" %% "slick" % "3.4.1",
        "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
        "org.mariadb.jdbc" % "mariadb-java-client" % "3.1.0",
        "org.slf4j" % "slf4j-simple" % "1.7.36", // DO NOT UPDATE WITHOUT CHANGING OTHER DEPENDENCIES
        "com.github.pureconfig" %% "pureconfig" % "0.17.2",
        "com.softwaremill.akka-http-session" %% "core" % "0.7.0",
        "com.lightbend.akka.management" %% "akka-management-cluster-http" % "1.2.0",
        "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.2.0",
        "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % "1.2.0",
        
        "org.scalatest" %% "scalatest" % "3.2.14" % Test,
        "org.scalamock" %% "scalamock" % "5.2.0" % Test,
        "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.7.0" % Test
    )
  )

addCommandAlias(
    "build",
    """|;
       |clean;
       |assembly;
  """.stripMargin)