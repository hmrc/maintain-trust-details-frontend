resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.typesafeRepo("releases")

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
// Try to remove when sbt 1.8.0+ and scoverage is 2.0.7+
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

addSbtPlugin("uk.gov.hmrc"         % "sbt-auto-build"        % "3.9.0")
addSbtPlugin("uk.gov.hmrc"         % "sbt-distributables"    % "2.2.0")
addSbtPlugin("com.typesafe.play"   % "sbt-plugin"            % "2.8.19")
addSbtPlugin("org.irundaia.sbt"    % "sbt-sassify"           % "1.5.1")
addSbtPlugin("org.scalastyle"     %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage"       % "sbt-scoverage"         % "2.0.7")
addSbtPlugin("net.ground5hark.sbt" % "sbt-concat"            % "0.2.0")
addSbtPlugin("com.typesafe.sbt"    % "sbt-uglify"            % "2.0.0")
addSbtPlugin("com.typesafe.sbt"    % "sbt-digest"            % "1.1.4")
addSbtPlugin("com.timushev.sbt"    % "sbt-updates"           % "0.6.3")
