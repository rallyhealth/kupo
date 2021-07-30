// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jasna.blemberg/code/kupo/conf/routes
// @DATE:Fri Jul 30 11:36:05 EDT 2021

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:7
package com.rallyhealth.kupo.controller.javascript {

  // @LINE:10
  class ReverseSlackController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def oauthCallback: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.rallyhealth.kupo.controller.SlackController.oauthCallback",
      """
        function(code0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "oauthcallback" + _qS([(""" + implicitly[play.api.mvc.QueryStringBindable[String]].javascriptUnbind + """)("code", code0)])})
        }
      """
    )
  
  }

  // @LINE:7
  class ReverseKupoController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def eventOrVerification: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.rallyhealth.kupo.controller.KupoController.eventOrVerification",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "botHook"})
        }
      """
    )
  
    // @LINE:8
    def interaction: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "com.rallyhealth.kupo.controller.KupoController.interaction",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "interaction"})
        }
      """
    )
  
  }


}
