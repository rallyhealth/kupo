// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jasna.blemberg/code/kupo/conf/routes
// @DATE:Fri Jul 30 11:36:05 EDT 2021

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:7
package com.rallyhealth.kupo.controller {

  // @LINE:10
  class ReverseSlackController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:10
    def oauthCallback(code:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "oauthcallback" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("code", code)))))
    }
  
  }

  // @LINE:7
  class ReverseKupoController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def eventOrVerification(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "botHook")
    }
  
    // @LINE:8
    def interaction(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "interaction")
    }
  
  }


}
