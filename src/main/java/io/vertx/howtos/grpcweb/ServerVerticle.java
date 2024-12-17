package io.vertx.howtos.grpcweb;

import io.grpc.stub.StreamObserver;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.grpc.server.GrpcServerOptions;
import io.vertx.grpcio.server.GrpcIoServer;
import io.vertx.grpcio.server.GrpcIoServiceBridge;

public class ServerVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    GrpcIoServer grpcServer = GrpcIoServer.server(vertx, new GrpcServerOptions().setGrpcWebEnabled(true));

    GreeterGrpc.GreeterImplBase service = new GreeterGrpc.GreeterImplBase() {
      @Override
      public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
        responseObserver.onCompleted();
      }
    };

    GrpcIoServiceBridge serverStub = GrpcIoServiceBridge.bridge(service);
    serverStub.bind(grpcServer);

    Router router = Router.router(vertx);
    router.route().consumes("application/grpc-web-text").handler(rc -> grpcServer.handle(rc.request()));
    router.get().handler(StaticHandler.create());

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ServerVerticle()).await();
  }
}
