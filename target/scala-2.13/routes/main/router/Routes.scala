// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jasna.blemberg/code/kupo/conf/routes
// @DATE:Fri Jul 30 11:36:05 EDT 2021

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:7
  KupoController_1: com.rallyhealth.kupo.controller.KupoController,
  // @LINE:10
  SlackController_0: com.rallyhealth.kupo.controller.SlackController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:7
    KupoController_1: com.rallyhealth.kupo.controller.KupoController,
    // @LINE:10
    SlackController_0: com.rallyhealth.kupo.controller.SlackController
  ) = this(errorHandler, KupoController_1, SlackController_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, KupoController_1, SlackController_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """botHook""", """com.rallyhealth.kupo.controller.KupoController.eventOrVerification()"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """interaction""", """com.rallyhealth.kupo.controller.KupoController.interaction()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """oauthcallback""", """com.rallyhealth.kupo.controller.SlackController.oauthCallback(code:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:7
  private[this] lazy val com_rallyhealth_kupo_controller_KupoController_eventOrVerification0_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("botHook")))
  )
  private[this] lazy val com_rallyhealth_kupo_controller_KupoController_eventOrVerification0_invoker = createInvoker(
    KupoController_1.eventOrVerification(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.rallyhealth.kupo.controller.KupoController",
      "eventOrVerification",
      Nil,
      "POST",
      this.prefix + """botHook""",
      """ An example com.rallyhealth.kupo.controller showing a sample home page""",
      Seq()
    )
  )

  // @LINE:8
  private[this] lazy val com_rallyhealth_kupo_controller_KupoController_interaction1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("interaction")))
  )
  private[this] lazy val com_rallyhealth_kupo_controller_KupoController_interaction1_invoker = createInvoker(
    KupoController_1.interaction(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.rallyhealth.kupo.controller.KupoController",
      "interaction",
      Nil,
      "POST",
      this.prefix + """interaction""",
      """""",
      Seq()
    )
  )

  // @LINE:10
  private[this] lazy val com_rallyhealth_kupo_controller_SlackController_oauthCallback2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("oauthcallback")))
  )
  private[this] lazy val com_rallyhealth_kupo_controller_SlackController_oauthCallback2_invoker = createInvoker(
    SlackController_0.oauthCallback(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "com.rallyhealth.kupo.controller.SlackController",
      "oauthCallback",
      Seq(classOf[String]),
      "GET",
      this.prefix + """oauthcallback""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:7
    case com_rallyhealth_kupo_controller_KupoController_eventOrVerification0_route(params@_) =>
      call { 
        com_rallyhealth_kupo_controller_KupoController_eventOrVerification0_invoker.call(KupoController_1.eventOrVerification())
      }
  
    // @LINE:8
    case com_rallyhealth_kupo_controller_KupoController_interaction1_route(params@_) =>
      call { 
        com_rallyhealth_kupo_controller_KupoController_interaction1_invoker.call(KupoController_1.interaction())
      }
  
    // @LINE:10
    case com_rallyhealth_kupo_controller_SlackController_oauthCallback2_route(params@_) =>
      call(params.fromQuery[String]("code", None)) { (code) =>
        com_rallyhealth_kupo_controller_SlackController_oauthCallback2_invoker.call(SlackController_0.oauthCallback(code))
      }
  }
}
