ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "FPLGame"
  )

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"
val EnumeratumDoobieVersion = "1.7.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari"   % DoobieVersion,
  "io.estatico"  %% "newtype"         % NewTypeVersion,
  "com.beachape" %% "enumeratum-doobie" % EnumeratumDoobieVersion
)