"use strict";
var connection = null;

function connect(wsUrl) {
  const requestId = document.getElementById("email").value;
  console.log("Creating WebSocket for " + requestId);

  /*var serverUrl;
  var scheme = "ws";
  const port = 9443;

  // If this is an HTTPS connection, we have to use a secure WebSocket
  // connection too, so add another "s" to the scheme.

  if (document.location.protocol === "https:") {
    scheme += "s";
  }

  serverUrl = scheme + "://" + document.location.hostname + ":" + port;

  const endpoint = serverUrl + "/api/listen/" + requestId;*/
  const endpoint = wsUrl.substring(0, wsUrl.indexOf(":id")) + requestId;

  connection = new WebSocket(endpoint, "json");
  console.log("***CREATED WEBSOCKET");

  connection.onopen = function(evt) {
    console.log("***ONOPEN");
    document.getElementById("email").disabled = true;
    document.getElementById("loginWithTruecallerButton").disabled = true;
    doSend("Authorization", "Yo!");
  };
  console.log("***CREATED ONOPEN");

  connection.onclose = function(evt) {
      console.log("***ONCLOSE");
      document.getElementById("email").disabled = false;
      document.getElementById("loginWithTruecallerButton").disabled = false;
      //document.getElementById("text").disabled = false;
      //document.getElementById("send").disabled = false;
  };
  console.log("***CREATED ONCLOSE");

  connection.onmessage = function(evt) {
    console.log("***ONMESSAGE");
    /*var f = document.getElementById("chatbox").contentDocument;
    var text = "";*/
    var msg = JSON.parse(evt.data);
    var time = new Date(msg.eventTime);
    var timeStr = time.toLocaleTimeString();
    console.log("Message received at " + timeStr + ": ");
    console.dir(msg);
  };
  console.log("***CREATED ONMESSAGE");
}

function send() {
  console.log("***SEND");
  doSend("text", document.getElementById("password").value);
}

function doSend(type, message) {
  console.log("***DO_SEND");
  if(connection) {
    var msg = {
      text: message,
      type: type,
      date: Date.now()
    };
    connection.send(JSON.stringify(msg));
  }
}

function handleKey(evt) {
  if (evt.keyCode === 13 || evt.keyCode === 14) {
    if (!document.getElementById("send").disabled) {
      send();
    }
  }
}