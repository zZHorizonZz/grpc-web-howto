package io.vertx.howtos.grpcweb;

import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.grpcio.server.GrpcIoServer;
import io.vertx.grpcio.server.GrpcIoServiceBridge;

public class ServerVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    // tag::grpcServer[]
    GreeterGrpc.GreeterImplBase service = new GreeterGrpc.GreeterImplBase() {
      @Override
      public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
        responseObserver.onCompleted();
      }
    };

    GrpcIoServer grpcServer = GrpcIoServer.server(vertx);
    GrpcIoServiceBridge serverStub = GrpcIoServiceBridge.bridge(service);
    serverStub.bind(grpcServer);
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
