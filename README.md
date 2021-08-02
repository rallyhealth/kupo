# Kupo

Kupo sends private, ephemeral messages to Slack users when it detects exclusionary language in a channel in which it is part. 
Kupo's messages are initially gentle suggestions for alternative, inclusive language. Kupo gives the option of providing 
additional background information on why their message was interpreted as exclusionary. If authorized, it can directly 
edit a user's message to replace it with Kupo's suggested alternative. Otherwise, it can be dismissed. Kupo can also be 
direct messaged if the user would like to test out their message to see if Kupo thinks it is exclusionary/inclusive.

Kupo helps everyone become better communicators on Slack by encouraging inclusive language automatically, while trying 
not to "get in the way" of conversation itself or "shame" people: Kupo's messages are always private and ephemeral, 
i.e., no one else can see them, and they are not part of the Slack chat history (they go away during each session 
refresh).

Equally important is that Kupo reduces the burden on those hurt by exclusionary language. People who get hurt often 
have a dilemma with whether or not they should confront the speaker (or "writer", in Slack's case). If they decide to, 
they risk conflict between themselves and the speaker, and possibly increased pain from being called "oversensitive" 
or just having their feelings dismissed somehow. There is also risk of shame, embarrassment, and/or defensiveness for 
the speaker: it's rarely a pleasant experience to be corrected or learn that you've hurt someone.

Overall, Kupo aims to make Slack spaces safer.

## To do
 
- Add tests
  - services, helpers, proper JSON serialization
- Persist feedback data ("Was this helpful?")  
- Build analytics controller that queries the WordCountStore for number of times an exclusionary
  word was flagged in a non-DM space.
- Convert in-memory WordStore to Redis, Postgres, etc.

## Contributing

Check out to-dos above or any open issues. Create a fork for pull requests (PRs). Add issues for improvements or bugs you'd like to
see addressed. Please tag [@JasnaMRB](https://github.com/JasnaMRB) and [@usufruct99](https://github.com/usufruct99).

## Setting up with Slack and a Remote Server

1. Create or get access to a Slack workspace.
1. Create a Slack app "profile" (my term) at https://api.slack.com/apps
  - Select the workspace from Step 1
  - Note in *Settings* -> *Basic Information* the **App ID**, **Client ID**, **Client Secret**, **Signing Secret**, and
    **Verification Token**
1. In [`application.conf`](../conf/application.conf), replace application configs with values from your Slack app's profile
   from the previous step.
1. Upload the application code to a server, with setup and [configurations](../conf/application.conf) as well for Redis.   
1. In *Features* -> *Event Subscriptions*,
    - turn **Enable Events** *on*.
    - replace **Request URL** with the `<your domain>/kupo/botHook`, e.g., `https://jasna.me/kupo/botHook`
    - In *Subscribe to bot events*, *Add Bot User Event* for the following:
        - `channel_created` with scope `channels:read`
        - `message.channels` with scope `channels:history`
        - `message.groups` with scope `groups:history`
        - `message.im` with scope `im:history`
1. In *Features* -> *OAuth & Permissions*,
    - Note the **Bot User OAuth Access Token** and **OAuth Access Token**
    - Add **Redirect URL** `<your domain>/kupo/oauthcallback`, e.g., `https://jasna.me/kupo/oauthcallback`
    - In *Scopes* -> *Bot Token Scopes*, **Add an OAuth Scope** for the following scopes:
        - `app_mentions:read`
        - `channels:history`
        - `channels:join`
        - `channels:read`
        - `chat:write`
        - `chat:write.customize`
        - `chat:write.public`
        - `commands`
        - `groups:history`
        - `groups:read`
        - `im:history`
        - `im:read`
        - `im:write`
    - In *Scopes* -> *User Token Scopes*, **Add an Oauth Scope** for the following scopes:
        - `chat:write`
1. In *Features* -> *Interactivity & Shortcuts*,
    - Turn **Interactivity** on
    - Add **Request URL** `<your domain>/kupo/interaction`, e.g., `https://jasna.me/kupo/interaction`
   

 
- [Local Development Steps](docs/local_development.md)
