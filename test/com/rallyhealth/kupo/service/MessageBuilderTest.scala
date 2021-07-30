package com.rallyhealth.kupo.service

import com.rallyhealth.kupo.data.store.SuggestionInfo
import com.rallyhealth.kupo.service.MessageBuilder.ActionIds
import com.rallyhealth.kupo.slack.{BlockMessage, ButtonAction, ButtonStyle}
import org.scalatest.{FunSpec, Matchers}

class MessageBuilderTest extends FunSpec with MessageBuilderTestFixtures with Matchers {

  describe("buildAuthorize") {
    it("should construct an Authorize button") {
      MessageBuilder.buildAuthorize(authUrl) shouldBe BlockMessage(
        mainPlainText = MessageBuilder.MainPlainText.authorize,
        buttonActions = Seq(
          ButtonAction(
            text = "Authorize",
            actionId = ActionIds.Authorize,
            url = Some(authUrl)
          )
        )
      )
    }
  }
  describe("buildSuggestion") {
    it("should construct a Suggestion message") {
      val suggestionInfo = SuggestionInfo(
        key = "aKey",
        suggestion = "aSuggestion",
        user = "user",
        newMessage = "a new message"
      )
      MessageBuilder.buildSuggestion(
        suggestionInfo = suggestionInfo,
        ts = "timestamp",
        userToken = "usertoken"
      ) shouldBe BlockMessage(
        mainPlainText = s"<@user>, instead of *aKey*, did you mean *aSuggestion*?",
        buttonActions = Seq(
          ButtonAction(
            text = "Edit my message",
            actionId = ActionIds.Edit,
            style = ButtonStyle.Primary,
            value = Some(s"""{"ts":"timestamp","message":"a new message","userToken":"usertoken"}""")
          ),
          ButtonAction(
            text = "Dismiss",
            actionId = ActionIds.Dismiss,
            style = ButtonStyle.Danger
          ),
          ButtonAction(
            text = "Learn More",
            actionId = ActionIds.LearnMore,
            value = Some("aKey")
          )
        )
      )
    }
  }
  describe("buildLearnMore") {
    it("should construct a LearnMore button") {
      MessageBuilder.buildLearnMore("someText") shouldBe BlockMessage(
        mainPlainText = "someText",
        buttonActions = Seq(
          ButtonAction(
            text = "Yes",
            actionId = ActionIds.WasThisHelpful.Yes,
            style = ButtonStyle.Default
          ), ButtonAction(
            text = "Unsure",
            actionId = ActionIds.WasThisHelpful.Unsure,
            style = ButtonStyle.Default
          ), ButtonAction(
            text = "No",
            actionId = ActionIds.WasThisHelpful.No,
            style = ButtonStyle.Default
          )
        )
      )
    }
  }
}

trait MessageBuilderTestFixtures {

  val authUrl: String = "someUrl"
}
