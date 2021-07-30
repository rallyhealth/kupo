package com.rallyhealth.kupo.server

import play.api.mvc.{PathBindable, QueryStringBindable}

/**
  * Any custom classes that need to be binded to a Play route path or query parameter should live in here.
  */
object Bindables {

  def bindPathAndQueryString[T](
    name: String,
    parse: String => T,
    serialize: T => String
  ): (PathBindable[T], QueryStringBindable[T]) = {
    val error = (k: String, e: Exception) => s"Cannot parse parameter $k as $name: ${e.getMessage}"
    (
      new PathBindable.Parsing[T](parse, serialize, error),
      new QueryStringBindable.Parsing[T](parse, serialize, error)
    )
  }
}
