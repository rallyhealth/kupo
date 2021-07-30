package com.rallyhealth.kupo.controller

import com.rallyhealth.kupo.data.store.WordCountStore
import play.api.mvc.{AbstractController, ControllerComponents}

import java.time.Instant

class AnalyticsController(
  cc: ControllerComponents,
  store: WordCountStore
) extends AbstractController(cc) {

  // Todo
  def countWords: List[Instant] = ???
}
