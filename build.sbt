import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.DefaultBuildSettings
import scoverage.ScoverageKeys

val appName = "maintain-trust-details-frontend"

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / majorVersion := 0

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    DefaultBuildSettings.scalaSettings,
    DefaultBuildSettings.defaultSettings(),
    ScoverageKeys.coverageExcludedPackages := "<empty>;.*Reverse.*;.*Routes.*;uk.gov.hmrc.BuildInfo;" +
      "config.*;testOnlyDoNotUseInAppConf.*;views.html.*;.*RichJsValue.*",
    ScoverageKeys.coverageMinimumStmtTotal := 92,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    inConfig(Test)(testSettings),
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
    libraryDependencies ++= AppDependencies(),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/maintaintrustdetailsfrontend-app.js" ->
        group(Seq(
          "javascripts/maintaintrustdetailsfrontend.js",
          "javascripts/autocomplete.js",
          "javascripts/libraries/location-autocomplete.min.js",
          "javascripts/iebacklink.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages := Seq(digest),
    Assets / pipelineStages := Seq(concat, uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("maintaintrustdetailsfrontend-*.js"),
    scalacOptions ++= Seq(
      "-Wconf:cat=unused-imports&src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    )
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dlogger.resource=logback-test.xml",
    "-Dconfig.resource=test.application.conf"
  )
)

addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle IntegrationTest/scalastyle")
