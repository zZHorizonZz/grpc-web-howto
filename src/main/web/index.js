const {HelloRequest} = require("./service_pb"); // <1>
const {GreeterClient} = require("./service_grpc_web_pb"); // <2>

const greeterClient = new GreeterClient("http://" + window.location.hostname + ":8080", null, null); // <3>

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
