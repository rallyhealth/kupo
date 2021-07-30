// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/jasna.blemberg/code/kupo/conf/routes
// @DATE:Fri Jul 30 11:36:05 EDT 2021


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
