"use strict";
var connection = null;
//var clientID = 0;

/*function setUsername() {
  console.log("***SETUSERNAME");
  var msg = {
    name: document.getElementById("name").value,
    date: Date.now(),
    id: clientID,
    type: "username"
  };
  connection.send(JSON.stringify(msg));
}*/

function connect() {
  var serverUrl;
  var scheme = "ws";
  const port = 9000;

  // If this is an HTTPS connection, we have to use a secure WebSocket
  // connection too, so add another "s" to the scheme.

  if (document.location.protocol === "https:") {
    scheme += "s";
  }

  serverUrl = scheme + "://" + document.location.hostname + ":" + port;

  const requestId = document.getElementById("email").value;
  console.log("Creating WebSocket for " + requestId);

  connection = new WebSocket(serverUrl + "/api/listen/" + requestId, "json");
  console.log("***CREATED WEBSOCKET");

  connection.onopen = function(evt) {
    console.log("***ONOPEN");
    document.getElementById("email").disabled = true;
    document.getElementById("loginWithTruecallerButton").disabled = true;
    //document.getElementById("text").disabled = false;
    //document.getElementById("send").disabled = false;
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
    var time = new Date(msg.date);
    var timeStr = time.toLocaleTimeString();
    console.log("Message received at " + timeStr + ": ");
    console.dir(msg);

    /*switch(msg.type) {
      case "id":
        clientID = msg.id;
        setUsername();
        break;
      case "username":
        text = "<b>User <em>" + msg.name + "</em> signed in at " + timeStr + "</b><br>";
        break;
      case "message":
        text = "(" + timeStr + ") <b>" + msg.name + "</b>: " + msg.text + "<br>";
        break;
      case "rejectusername":
        text = "<b>Your username has been set to <em>" + msg.name + "</em> because the name you chose is in use.</b><br>";
        break;
      case "userlist":
        var ul = "";
        var i;

        for (i=0; i < msg.users.length; i++) {
          ul += msg.users[i] + "<br>";
        }
        document.getElementById("userlistbox").innerHTML = ul;
        break;
    }

    if (text.length) {
      f.write(text);
      document.getElementById("chatbox").contentWindow.scrollByPages(1);
    }*/
  };
  console.log("***CREATED ONMESSAGE");
}

function send() {
  console.log("***SEND");
  /*var msg = {
    text: document.getElementById("password").value,
    type: "message",
    id: clientID,
    date: Date.now()
  };
  connection.send(JSON.stringify(msg));
  document.getElementById("text").value = "";*/
  connection.send(document.getElementById("password").value);
}

function handleKey(evt) {
  if (evt.keyCode === 13 || evt.keyCode === 14) {
    if (!document.getElementById("send").disabled) {
      send();
    }
  }
}