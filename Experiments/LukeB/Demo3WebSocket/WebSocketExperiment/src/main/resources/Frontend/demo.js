var ws;

let running = false;

function start() {
    var refName = document.getElementById("refName").value;
    var url = "ws://localhost:8080/display/" + refName;
    //var url = "ws://echo.websocket.org";

    ws = new WebSocket(url);

    ws.onmessage = function(event) { // Called when client receives a message from the server
        console.log(event.data);

        // Parse the msg as a list
         let responseArr = JSON.parse(event.data)

         // Get the demo div
         let demoDiv = document.getElementById("demoDiv");

         // Change the divs position
         demoDiv.style.left = responseArr[0] * 5 + "px";
         demoDiv.style.top = (responseArr[1]  * 5 )+ 50 + "px";

         // Change the color
         demoDiv.style.backgroundColor = responseArr[2];

    };

    if(running == false){
        // Request a change every two seconds
        setInterval(function(){send()},750);
        running = true
    }

}

function send() {  // this is how to send messages
    console.log("Sending request")
    var content = "Arbitrary"
    ws.send(content);
}


