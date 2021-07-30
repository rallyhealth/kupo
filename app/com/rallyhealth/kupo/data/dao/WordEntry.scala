package com.rallyhealth.kupo.data.dao

import java.time.Instant

/**
  * @param teamId For entries across multiple Slack Workspaces
  */
case class WordEntryKey(
  word: String,
  teamId: String
) {
  val asString: String = s"$teamId.$word"
}

case class WordEntry(
  key: WordEntryKey,
  instances: List[Instant]
)
