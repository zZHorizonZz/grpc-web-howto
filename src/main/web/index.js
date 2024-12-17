const {HelloRequest} = require("./service_pb");
const {GreeterClient} = require("./service_grpc_web_pb");

const greeterClient = new GreeterClient("http://" + window.location.hostname + ":8080", null, null);

function sayHello(name, callback) {
  const request = new HelloRequest();
  request.setName(name);
  greeterClient.sayHello(request, {}, (err, response) => {
    if (err) {
      callback.call(null, `Unexpected error for sayHello: code = ${err.code}` + `, message = "${err.message}"`);
    } else {
      callback.call(null, response.getMessage());
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("send").addEventListener("click", () => {
    const name = document.getElementById("name").value;
    sayHello(name, msg => {
      document.getElementById("msg").innerText = msg;
    });
  });
});
