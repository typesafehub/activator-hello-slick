// This plugin adds forked run capabilities to Play projects which is needed for Activator.

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.play" % "sbt-fork-run-plugin" % "2.3.9")
