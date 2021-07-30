// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jasna.blemberg/code/kupo/conf/routes
// @DATE:Fri Jul 30 11:36:05 EDT 2021

package com.rallyhealth.kupo.controller;

import router.RoutesPrefix;

public class routes {
  
  public static final com.rallyhealth.kupo.controller.ReverseSlackController SlackController = new com.rallyhealth.kupo.controller.ReverseSlackController(RoutesPrefix.byNamePrefix());
  public static final com.rallyhealth.kupo.controller.ReverseKupoController KupoController = new com.rallyhealth.kupo.controller.ReverseKupoController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final com.rallyhealth.kupo.controller.javascript.ReverseSlackController SlackController = new com.rallyhealth.kupo.controller.javascript.ReverseSlackController(RoutesPrefix.byNamePrefix());
    public static final com.rallyhealth.kupo.controller.javascript.ReverseKupoController KupoController = new com.rallyhealth.kupo.controller.javascript.ReverseKupoController(RoutesPrefix.byNamePrefix());
  }

}
