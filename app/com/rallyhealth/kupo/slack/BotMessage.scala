package com.rallyhealth.kupo.slack

import com.rallyhealth.kupo.slack.Message.ObjType
import enumeratum.{EnumEntry, PlayEnum}
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsArray, JsString, Json}

sealed trait BotMessage {

  def asJsArray: JsArray
}

case class SimpleMessage(
  text: String
) extends BotMessage {

  override def asJsArray: JsArray = Json.arr(Json.obj("text" -> JsString(text)))
}

/**
 * See https://api.slack.com/reference/block-kit/block-elements#button
 */
sealed abstract class ButtonStyle(
  override val entryName: String
) extends EnumEntry

object ButtonStyle extends PlayEnum[ButtonStyle] {

  override def values: IndexedSeq[ButtonStyle] = findValues

  case object Primary extends ButtonStyle("primary")

  case object Default extends ButtonStyle(null)

  case object Danger extends ButtonStyle("danger")

}

/**
 * @param value Must be a string if present; can't be `null`
 */
case class ButtonAction(
  text: String,
  actionId: String,
  style: ButtonStyle = ButtonStyle.Default,
  value: Option[String] = None,
  url: Option[String] = None
)

case class BlockMessage(
  mainPlainText: String,
  buttonActions: Seq[ButtonAction]
) extends BotMessage {

  override def asJsArray: JsArray = {
    val textBlock = Json.obj(
      ObjType.Section,
      "text" -> Json.obj(
        ObjType.Markdown,
        "text" -> mainPlainText
      ),
    )
    val actions = JsArray(
      buttonActions
        .map { action =>
          val initial = Json.obj(
            ObjType.Button,
            "text" -> Json.obj(
              ObjType.PlainText,
              "text" -> action.text,
            ),
            "action_id" -> action.actionId
          )
          val withMaybeValue = action.value.map(value => initial + ("value", JsString(value))).getOrElse(initial)
          val withMaybeStyle = if (action.style != ButtonStyle.Default) withMaybeValue +
            ("style", JsString(action.style.entryName)) else withMaybeValue
          val withMaybeUrl = action.url.map(u => withMaybeStyle + ("url", JsString(u))).getOrElse(withMaybeStyle)
          withMaybeUrl
        }
    )
    val actionsBlock = Json.obj(
      ObjType.Actions,
      "block_id" -> "suggestion.actions",
      "elements" -> actions
    )
    Json.arr(textBlock, actionsBlock)
  }
}

object Message {

  object ObjType {

    private val typ = "type"
    val Markdown: (String, JsValueWrapper) = typ -> "mrkdwn"
    val PlainText: (String, JsValueWrapper) = typ -> "plain_text"
    val Section: (String, JsValueWrapper) = typ -> "section"
    val Button: (String, JsValueWrapper) = typ -> "button"
    val Actions: (String, JsValueWrapper) = typ -> "actions"
  }

}
