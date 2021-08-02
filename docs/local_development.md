# Local Development

## Initial setup

### Data

1. Start up a [Redis server](https://formulae.brew.sh/formula/redis).
1. Set the host and port in the Redis configs at [`application.conf`](../conf/application.conf)

### Main servers

I use ngrok for a temporary local server, but you can use whatever you want.

These steps walk you through creating a Slack app for Kupo since Kupo is not a distributed
app. It's up to the developer to host this, but if you do, please give credit where it's due!


### The fun begins

__Prerequisite (other than the above)__: you have already [set up a Slack workspace](../README.md#Setting up with Slack)

1. Start up the server: in the code directory, run `$ sbt run`
1. Install ngrok and run it:
  ```shell script
   # assuming you're in the parent directory:
   $ ./ngrok http 9000 # runs on port 9000
   ```
- This will start an ngrok server that'll forward requests to your local Kupo server. You should see something like this in your console:
  ```shell script
    ngrok by @inconshreveable                                                                         (Ctrl+C to quit)
                                                                                                                                                                    
    Session Status                online                                                                                                                            
    Account                       Jasna (Plan: Free)                                                                                                                
    Version                       2.3.35                                                                                                                            
    Region                        United States (us)                                                                                                                
    Web Interface                 http://127.0.0.1:4040                                                                                                             
    Forwarding                    http://e1c2c7f7eaa3.ngrok.io -> http://localhost:9000                                                                             
    Forwarding                    https://e1c2c7f7eaa3.ngrok.io -> http://localhost:9000                                                                            
                                                                                                                                                                    
    Connections                   ttl     opn     rt1     rt5     p50     p90                                                                                       
                                  0       0       0.00    0.00    0.00    0.00   
   ```
1. In the main code directory, run `$ sbt run`
1. Follow the same instructions for [setting up a Slack workspace](../README.md#Setting up with Slack),
   replacing the domains with your https ngrok domain, e.g.,  `https://e1c2c7f7eaa3.ngrok.io`.

## After initial setup

Assuming you use ngrok, since the domain is randomly generated at each boot-up:

1. In the code directory, run `$ sbt run` (if not booted up already)
1. Boot up ngrok again (if not booted up already)
1. In https://api.slack.com -> (your app), update URLs for **Event Subscriptions**, **Interactivity & Shortcuts**, and
   **OAuth & Permissions** with your current ngrok domain

