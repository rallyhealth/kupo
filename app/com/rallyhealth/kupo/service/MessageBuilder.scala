package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.data.store.SuggestionInfo
import com.rallyhealth.kupo.slack.{BlockMessage, ButtonAction, ButtonStyle}
import play.api.libs.json.{Json, Reads}

object MessageBuilder {

  def buildAuthorize(oauthSignInUrl: String): BlockMessage = {
    BlockMessage(
      mainPlainText = MainPlainText.authorize,
      buttonActions = Seq(Buttons.buildAuthorize(oauthSignInUrl))
    )
  }

  /**
   * Message created and sent when user clicks "Learn More" button.
   *
   * @param text Helpful text to explain detection of exclusive language.
   * @return Message with text and "Was this helpful?" buttons
   */
  def buildLearnMore(text: String): BlockMessage = {
    BlockMessage(
      mainPlainText = text,
      buttonActions = Seq(Buttons.HelpfulYes, Buttons.HelpfulUnsure, Buttons.HelpfulNo)
    )
  }

  /**
   * Message created and sent when user's message is detected to have exclusive language.
   *
   * @param ts Timestamp of user's message
   */
  def buildSuggestion(suggestionInfo: SuggestionInfo, ts: String, userToken: String): BlockMessage = {
    BlockMessage(
      mainPlainText = suggestionInfo.fullSuggestion,
      buttonActions = Seq(
        Buttons.buildEdit(EditInfo(message = suggestionInfo.newMessage, ts = ts, userToken = userToken)),
        Buttons.Dismiss,
        Buttons.buildLearnMore(suggestionInfo)
      )
    )
  }

  // Should these be externalized?
  object MainPlainText {

    val authorize: String = "I can edit your message for you if you authorize me. Otherwise," +
      " you can edit your message manually or ignore me--but I hope you don't ignore me :slightly_frowning_face:."
    /**
     * Simple string message sent when user's direct message is not detected to have exclusive language.
     */
    val directMessagePasses: String = "Your message passes my test. But, please remember: I'm just a simple bot and " +
      "can't capture all intricacies of human (English) language."
  }

  object Buttons {

    private[service] def buildAuthorize(oauthSignUrl: String) =
      ButtonAction(
        text = "Authorize",
        actionId = ActionIds.Authorize,
        url = Some(oauthSignUrl)
      )

    private[service] def buildLearnMore(suggestionInfo: SuggestionInfo) =
      ButtonAction(
        text = "Learn More",
        actionId = ActionIds.LearnMore,
        value = Some(suggestionInfo.key)
      )

    private[service] def buildEdit(correctInfo: EditInfo): ButtonAction =
      ButtonAction(
        text = "Edit my message",
        actionId = ActionIds.Edit,
        style = ButtonStyle.Primary,
        value = Some(correctInfo.asStringifiedJson)
      )

    private val Close: ButtonAction = ButtonAction(
      text = "Close",
      actionId = ActionIds.Close,
      style = ButtonStyle.Default
    )

    private[service] val Dismiss: ButtonAction = ButtonAction(
      text = "Dismiss",
      actionId = ActionIds.Dismiss,
      style = ButtonStyle.Danger
    )

    private[service] val HelpfulNo: ButtonAction = ButtonAction(
      text = "No",
      actionId = ActionIds.WasThisHelpful.No,
      style = ButtonStyle.Default
    )

    private[service] val HelpfulYes: ButtonAction = ButtonAction(
      text = "Yes",
      actionId = ActionIds.WasThisHelpful.Yes,
      style = ButtonStyle.Default
    )

    private[service] val HelpfulUnsure: ButtonAction = ButtonAction(
      text = "Unsure",
      actionId = ActionIds.WasThisHelpful.Unsure,
      style = ButtonStyle.Default
    )
  }

  object ActionIds {

    val Authorize: String = "button.authorize"
    val Close: String = "button.close"
    val Edit: String = "button.edit"
    val Dismiss: String = "button.dismiss"
    val LearnMore: String = "button.learnmore"

    object WasThisHelpful {

      val No: String = "button.helpful.no"
      val Unsure: String = "button.helpful.unsure"
      val Yes: String = "button.helpful.yes"
      val asSet: Set[String] = Set(No, Yes, Unsure)
    }

  }

}

/**
 * @param ts      The timestamp of the original message to correct/edit
 * @param message The new message
 */
case class EditInfo(
  ts: String,
  message: String,
  userToken: String
) {

  /**
   * @return Json as String, since [[ButtonAction.value]] requires it.
   */
  def asStringifiedJson: String = s"""{"ts":"$ts","message":"$message","userToken":"$userToken"}"""
}

object EditInfo {

  implicit val reads: Reads[EditInfo] = Json.reads[EditInfo]
}
