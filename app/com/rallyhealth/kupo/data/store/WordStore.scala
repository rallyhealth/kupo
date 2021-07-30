package com.rallyhealth.kupo.data.store

case class LearnMoreLink(
  url: String,
  displayText: String
)

case class LearnMore(
  mainText: String,
  links: Set[LearnMoreLink] = Set.empty
) {

  private val linksAsString: String = {
    def interpolate(link: LearnMoreLink) = s"\n - <${link.url}|${link.displayText}>"

    if (links.nonEmpty)
      s"\n\nSee also:\n${links.map(interpolate).mkString}"
    else ""
  }
  val asString: String = s"$mainText$linksAsString \n\nWas this message helpful?"
}

/**
 * In-Memory DB of exclusionary phrases with helpful responses.
 * Hardcoded for simplicity and speed.
 */
object ExclusionaryWordStore {

  private def buildGuyEntry(precedingPhrase: String): SuggestionBody = SuggestionBody(
    Array(
      s"$precedingPhrase folks",
      s"$precedingPhrase friends",
      s"$precedingPhrase team",
      s"$precedingPhrase peeps",
      s"$precedingPhrase people",
      s"$precedingPhrase buds"
    ),
    learnMore = LearnMore(
      mainText = "*guys* is not a gender-neutral word, as much as we like to convince ourselves that it is (related: " +
        "it is unheard of to call an _individual woman_ a *guy*). *guys* perpetuates the sort of _default-male_ unconscious " +
        "(or conscious) bias in the way we view human beings in general, e.g., *mankind* vs. *humankind*. " +
        "\n\n*guys* is especially exclusive when used to address a group of mixed-gender people, " +
        "or even a group in which no one actually identifies as a _guy_, i.e., _male_. You may be literally referring " +
        "to a group of all males, but you may not--it is possible that someone in the group hasn't come out as having a " +
        "different gender identity than what you assume they are. " +
        "\n\nFurthermore, language is _habitual_. Even if you know " +
        "for sure you are literally addressing a group of all males at this moment, you may still have a habit of saying " +
        "*guys* in other less appropriate moments. " +
        "\n\nPlease try to make a habit of using more gender-inclusive alternatives to *guys*. ",
      links = Set(
        LearnMoreLink(
          "https://www.theatlantic.com/family/archive/2018/08/guys-gender-neutral/568231/",
          "The Atlantic: The Problem with 'Hey Guys'"
        ),
        LearnMoreLink(
          "https://www.huffpost.com/entry/gendered-language-hey-guys_l_5f21b189c5b6b8cd63b0f331",
          "Huff Post: Instead of Saying 'Hey Guys!' at Work, Try These Gender-Neutral Alternatives"
        )
      )
    )
  )

  private val gayEntry: SuggestionBody = SuggestionBody(
    suggestions = Array("that's not cool"),
    learnMore = LearnMore(
      mainText = "Using *gay* in a pejorative way has historic homophobic, anti-LGBTQ* problems. Please refrain from using *gay* in a negative way. " +
        "\n\n You can, of course, use *gay* in a positive way, all you want! :rainbow: :pride: " +
        "\n\n (And in the off-chance that you did mean *gay* in a positive way, please excuse me for flagging you on accident." +
        "My 'AI' tends to err on the side of protecting those commonly oppressed, including the LGBTQ+ community.)",
      links = Set(
        LearnMoreLink(
          url = "https://www.psychologytoday.com/us/blog/understanding-the-erotic-code/201803/thats-so-gay-is-just-so-wrong",
          displayText = "Psychology Today: \"That's so gay\" is just so wrong"
        ),
        LearnMoreLink(
          url = "https://www.opendemocracy.net/en/dont-call-me-homophobic-complexity-of-thats-so-gay/",
          displayText = "openDemocracy: Don't Call me homophobic - the complexity of 'that's so gay'"
        )
      ),
    )
  )

  private val gyppedEntry: SuggestionBody = SuggestionBody(
    suggestions = Array("swindled", "cheated"),
    learnMore = LearnMore(
      mainText =
        "_gypped_ unfortunately comes from a racial slur towards the Romani people. It would be best to avoid " +
          "this phrase. Please instead use an alternative.",
      links = Set(
        LearnMoreLink(
          url = "https://www.npr.org/sections/codeswitch/2013/12/30/242429836/why-being-gypped-hurts-the-roma-more-than-it-hurts-you",
          displayText = "NPR - Code Switch: Why Being 'Gypped' Hurts The Roma More Than It Hurts You"
        ),
        LearnMoreLink(
          url = "https://www.babbel.com/en/magazine/common-racist-words-phrases",
          displayText = "Babbel: 11 Common English Words And Phrases With Racist Origins"
        )
      )
    )
  )

  val wordStore: Map[String, SuggestionBody] = Map.apply(
    ("hey guys", buildGuyEntry("hey")),
    ("hey, guys", buildGuyEntry("hey")),
    ("hi guys", buildGuyEntry("hi")),
    ("hi, guys", buildGuyEntry("hi")),
    ("yo guys", buildGuyEntry("yo")),
    ("engineering guys", buildGuyEntry("engineering")),
    ("security guys", buildGuyEntry("security")),
    ("it guys", buildGuyEntry("IT")),
    ("product guys", buildGuyEntry("product")),
    ("guys,", buildGuyEntry("")),
    ("that's gay", gayEntry),
    ("that's so gay", gayEntry),
    ("gypped", gyppedEntry),
    ("jipped", gyppedEntry),
    ("redskins", SuggestionBody(
      suggestions = Array("Washington Football Team"),
      learnMore = LearnMore(
        mainText =
          "Unfortunately *redskins* originates from a racial slur towards Indigenous or Native American people." +
            " While it has historically been the name of the Washington, DC football team, please refrain from referring " +
            "to the team with this slur in our Slack workspace and instead use the official new name, the Washington Football team.",
        links = Set(
          LearnMoreLink(
            url = "https://www.washingtonfootball.com/",
            displayText = "Washington Football Team Official Site"
          ),
          LearnMoreLink(
            url = "https://www.washingtonpost.com/sports/2020/07/23/washington-redskins-new-team-name-washington-football-team/",
            displayText = "NFL franchise to go by 'Washington Football Team' this season, delaying permanent name change"
          )
        )

      )
    )),
    ("preferred pronouns", SuggestionBody(
      suggestions = Array("pronouns"),
      learnMore = LearnMore(
        mainText = "Saying _preferred_ pronouns implies that one's pronouns are optional. Please just say *pronouns* instead.",
        links = Set(
          LearnMoreLink(
            url = "https://www.forbes.com/sites/ashleefowlkes/2020/02/27/why-you-should-not-say-preferred-gender-pronouns/#5f34fd11bd60",
            displayText = "Forbes: Why You Should Not Say 'Preferred Gender Pronouns'"
          )
        )
      )
    ))
  )
}

case class SuggestionBody(
  suggestions: Array[String],
  learnMore: LearnMore
)

case class SuggestionInfo(
  key: String,
  suggestion: String,
  user: String,
  newMessage: String
) {

  val fullSuggestion: String = s"<@$user>, instead of *$key*, did you mean *$suggestion*?"
}
