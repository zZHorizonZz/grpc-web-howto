package io.vertx.howtos.grpcweb;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.grpc.server.GrpcServer;

public class ServerVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    // tag::grpcServer[]
    VertxGreeterGrpcServer.GreeterApi stub = new VertxGreeterGrpcServer.GreeterApi() {
      @Override
      public Future<HelloReply> sayHello(HelloRequest request) {
        return Future.succeededFuture(HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
      }
    };

    GrpcServer grpcServer = GrpcServer.server(vertx);
    stub.bindAll(grpcServer);
    // end::grpcServer[]

    // tag::routerAndServer[]
    Router router = Router.router(vertx);
    router.route()
      .consumes("application/grpc-web-text") // <1>
      .handler(rc -> grpcServer.handle(rc.request()));

    router.get().handler(StaticHandler.create()); // <2>

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);
    // end::routerAndServer[]
  }

  // tag::main[]
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ServerVerticle()).await();
    System.out.println("Server started, browse to http://localhost:8080");
  }
  // end::main[]
}
