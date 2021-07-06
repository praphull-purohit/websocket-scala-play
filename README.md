# Play Scala Websocket server and client demo

This project contains a server with websocket support and a client application that interacts with the websocket API.

# Details

## Login page
Email field from Login page (/login) is used as the websocket request identifier.

- Truecaller button initates the websocket.
- Github button sends a payload over active websocket connection. The data field of this payload are the contents of password field

## WebSocket workflow
- Initiate a websocket using `/api/listen/:id` API.
- This creates a WebsocketActor, and registers the id and actor with WebsocketManager. WebsocketManager watches for WebsocketActor termination and accordingly updates its subscription list
- Any incoming messages in an active web socket (strigified JSON of type `IncomingRequest`) are sent to WebSocketActor instance and processed
- WebSocketActor ignores any IncomingRequest messages until it has received an IncomingRequest of type `Authorization` containing a token in `text` field.
- Once a valid Authorization message is received, WebSocketActor echoes the incoming messages as OutgoingResponse

## WebSocket Manager
- Any internal service that needs to interact with an active websocket, needs to do so via WebSocket Manager
- WebSocketManager has active list of `WebSocketActor`s, which can be addressed via their id
- Any internal service actions (e.g. ones triggered by `/api/respond/:id`) can send `Notify` message to WebSocketManager, and it'll pass it to appropriate `WebSocketActor`, if such an actor is active
