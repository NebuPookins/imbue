# imbue
Simple Websocket based chat in Kotlin

I'm mainly using this repo as a basis for a "live coding" presentation where I try to show the difference writing code
in Kotlin vs in Java.

## Recommended Live Coding Agenda:

* `git checkout` this repo.
* Delete everything in the `src/main/kotlin` folder.
* @0:00 Pre-Introduction
    * Need to go "a little quickly" because: a lot of material to cover.
    * Feel free to interrupt me with questions
      * I might only be able to answer tersely and follow up with you later after the talk.
    * There's a lot of "theory" behind the design of the Kotlin programming language.
    * I love the theory, but I do not think I will have time to go into it this talk.
    * If there's interest, I can give another talk on the theory another time.
* +1:00 = @1:00 Introduction: What is this presentation about? What is the goal?
    * What: We're going to build a chat app using Kotlin.
    * Why 1: Convince you that Kotlin can help eliminate bugs that would have slipped through if you wrote this in Java.
      * How 1: This is live coding. Bugs are guaranteed to happen. But I bet that almost every bug I introduce is going
        to be caught by the compiler.
    * +1:00 = @2:00 Why 2: Easy to learn.
      * How 2: You've never seen a line of Kotlin before. This presentation is in Kotlin. You will be able to follow
        along anyway.  
* +1:00 = @3:00 Show the `pom.xml` file and talk very briefly about each dependency
  * `org.jetbrains.kotlin:kotlin-stdlib-jdk8` 
  * `com.sparkjava:spark-kotlin`
  * `ch.qos.logback:logback-classic`
  * `com.beust:klaxon`
  * `org.apache.commons:commons-lang3`
* +1:00 = @4:45 Write a Kotlin program that prints `Hello world!`
* +0:30 = @5:15 Briefly show the contents of the `index.html` and `index.js` files. Say you'll be explaining the client-server
  protocol as you go along.
* +1:15 = @6:30 implement an HTTP server that serves the contents of the `public` folder.
  * Demo in web browser.
  * Show error logs.
    * Browser: `Firefox can’t establish a connection to the server at ws://localhost:4567/ws.`
    * Server: `spark.http.matching.MatcherFilter - The requested route [/ws] has not been mapped in Spark for Accept: [text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8]`
* +1:15 = @7:45 Implement WebSocket handler that just does `TODO("not implemented")` in response to everything.
  * Demo in web browser.
    * Show that browser logs says `Websocket open`
    * Show stack trace in server (problem is `onWebSocketError`)
* +3:00 = @10:45 Implement `onWebSocketError` to log
  * Demo in web browser.
    * Show that browser logs says `Websocket open`
    * Show stack trace in server (problem is `onWebSocketConnect`)
* +3:00 = @13:45 Requirement: When a user connects to the chat room, the server should assign them a randomly generated name and
  send the user a message of the form `{"type": "SetName", "name": "Whatever their name is"}` so that the UI can update and show
  that name.
  * Demo in browser by connecting and showing name is set.
* +21:15 = @35:00 Requirement: When a user connects to a chat room, all users should receive a message of the form
  `{"type": "AddSystemLine", "line": "User 123 has joined the room."}`
  * Demo in browser by opening two tabs and showing users see each other join.
* +10:00 = @45:00  Requirement: When a user types in a line, the client will send a message of the form
  `{"type":"Message","payload":"Hello"}`. In response to this, the server should broadcast a message of the form
  `{"type":"AddUserLine","from":"Sender's Name","line":"Hello"}`.
  * Demo in browser by opening two tabs, typing in one tab and showing in the message show up in other.
* +10:00 = @55:00 run `ifconfig` to get IP address and let audience connect and play around.
* Optional (time permitting): Handle disconnections. Need to handle `onWebSocketClose`, need to catch exception when
  broadcasting (map to Either?)
* Optional (time permitting): Handle timeouts. Server should periodically send `{"type":"KeepAlive"}` to client, which
  will respond with `{"type":"KeepAlive"}`. Talk about `Nothing` type.