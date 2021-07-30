package com.rallyhealth.kupo.data.dao

/**
  *  @param clientId For entries across multiple apps (in case the same app has different clientIds in different workspaces)
  * @param teamId For entries across multiple Slack Workspaces
  */
case class UserTokenEntryKey(
  username: String,
  teamId: String,
  clientId: String
) {
  val asString: String = s"$teamId.$clientId.$username"
}

case class UserTokenEntry(
  key: UserTokenEntryKey,
  token: String
)
