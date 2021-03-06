import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.DefaultBuildSettings

val appName = "maintain-trust-details-frontend"

val silencerVersion = "1.7.0"

val excludedPackages = Seq(
  "<empty>",
  ".*Reverse.*",
  ".*Routes.*",
  ".*standardError*.*",
  ".*main_template*.*",
  "uk.gov.hmrc.BuildInfo",
  "app.*",
  "prod.*",
  "config.*",
  "testOnlyDoNotUseInAppConf.*",
  "views.html.*",
  "testOnly.*",
  "com.kenshoo.play.metrics*.*",
  ".*LocalDateService.*",
  ".*LocalDateTimeService.*",
  ".*RichJsValue.*",
  ".*Repository.*"
)

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimum := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    DefaultBuildSettings.scalaSettings,
    DefaultBuildSettings.defaultSettings(),
    scoverageSettings,
    scalaVersion := "2.12.12",
    // ***************
    // Use the silencer plugin to suppress warnings
    // You may turn it on for `views` too to suppress warnings from unused imports in compiled twirl templates, but this will hide other warnings.
    scalacOptions += "-P:silencer:pathFilters=routes;views",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    // ***************
    SbtDistributablesPlugin.publishingSettings,
    inConfig(Test)(testSettings),
    majorVersion := 0,
    name := appName,
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 9838,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    dependencyOverrides ++= AppDependencies.overrides,
    resolvers ++= Seq(
      Resolver.jcenterRepo
    ),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/maintaintrustdetailsfrontend-app.js" ->
        group(Seq(
          "javascripts/maintaintrustdetailsfrontend.js",
          "javascripts/autocomplete.js",
          "javascripts/libraries/location-autocomplete.min.js",
          "javascripts/iebacklink.js",
          "javascripts/print.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages := Seq(digest),
    pipelineStages in Assets := Seq(concat, uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("maintaintrustdetailsfrontend-*.js")
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dlogger.resource=logback-test.xml",
    "-Dconfig.resource=test.application.conf"
  )
)
