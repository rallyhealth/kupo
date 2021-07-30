import com.rallyhealth.kupo.controller.{ActionModule, ControllerModule}
import com.rallyhealth.kupo.data.RedisModule
import com.rallyhealth.kupo.service.ServiceModule
import com.rallyhealth.kupo.slack.SlackClientModule
import com.softwaremill.macwire.wire
import controllers.AssetsComponents
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient
import router.Routes

import scala.concurrent.Future

class KupoApplicationLoader extends ApplicationLoader {

  def load(context: Context): Application = new KupoComponents(context).application
}

class KupoComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AssetsComponents {

  // Logger must be manually configured with a custom ApplicationLoader
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  val wsClient = new StandaloneAhcWSClient(new DefaultAsyncHttpClient)

  applicationLifecycle.addStopHook(
    () => Future.successful {
      wsClient.close()
    }
  )

  // Modules
  lazy val slackClientModule: SlackClientModule = wire[SlackClientModule]
  lazy val actionModule: ActionModule = wire[ActionModule]
  lazy val controllerModule: ControllerModule = wire[ControllerModule]
  lazy val redisModule: RedisModule = wire[RedisModule]
  lazy val serviceModule: ServiceModule = wire[ServiceModule]

  lazy val router: Router = {
    val prefix: String = "/" // add the prefix string in local scope for the Routes constructor
    wire[Routes]
  }
}
