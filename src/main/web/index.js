// Import HelloRequest object from JS generated file
const {HelloRequest} = require("./service_pb");
// Import GreeterClient object from gRPC Web (JS) generated file
const {GreeterClient} = require("./service_grpc_web_pb");

// Configure to client to send requests to the web server
const greeterClient = new GreeterClient("http://" + window.location.hostname + ":8080", null, null);

export function sayHello() {
  const request = new HelloRequest();
  request.setName(document.getElementById("name").value);
  greeterClient.sayHello(request, {}, (err, response) => {
    const msgElem = document.getElementById("msg");
    if (err) {
      msgElem.innerText = `Unexpected error for sayHello: code = ${err.code}` + `, message = "${err.message}"`;
    } else {
      msgElem.innerText = response.getMessage();
    }
  });
  return false; // prevent form posting
}
