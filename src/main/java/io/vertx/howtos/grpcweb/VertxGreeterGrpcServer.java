package io.vertx.howtos.grpcweb;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;
import io.vertx.grpc.common.GrpcStatus;
import io.vertx.grpc.common.ServiceName;
import io.vertx.grpc.common.ServiceMethod;
import io.vertx.grpc.common.GrpcReadStream;
import io.vertx.grpc.common.GrpcWriteStream;
import io.vertx.grpc.common.GrpcMessageDecoder;
import io.vertx.grpc.common.GrpcMessageEncoder;
import io.vertx.grpc.server.GrpcServerResponse;
import io.vertx.grpc.server.GrpcServer;

import java.util.ArrayList;
import java.util.List;

public class VertxGreeterGrpcServer  {

  public static final ServiceMethod<io.vertx.howtos.grpcweb.HelloRequest, io.vertx.howtos.grpcweb.HelloReply> SayHello = ServiceMethod.server(
    ServiceName.create("helloworld", "Greeter"),
    "SayHello",
    GrpcMessageEncoder.encoder(),
    GrpcMessageDecoder.decoder(io.vertx.howtos.grpcweb.HelloRequest.parser()));
  public static final ServiceMethod<io.vertx.howtos.grpcweb.HelloRequest, io.vertx.howtos.grpcweb.HelloReply> SayHello_JSON = ServiceMethod.server(
    ServiceName.create("helloworld", "Greeter"),
    "SayHello",
    GrpcMessageEncoder.json(),
    GrpcMessageDecoder.json(() -> io.vertx.howtos.grpcweb.HelloRequest.newBuilder()));

  public static class GreeterApi {

    public Future<io.vertx.howtos.grpcweb.HelloReply> sayHello(io.vertx.howtos.grpcweb.HelloRequest request) {
      throw new UnsupportedOperationException("Not implemented");
    }
    public void sayHello(io.vertx.howtos.grpcweb.HelloRequest request, Promise<io.vertx.howtos.grpcweb.HelloReply> response) {
      sayHello(request)
        .onSuccess(msg -> response.complete(msg))
        .onFailure(error -> response.fail(error));
    }

    public final void handle_sayHello(io.vertx.grpc.server.GrpcServerRequest<io.vertx.howtos.grpcweb.HelloRequest, io.vertx.howtos.grpcweb.HelloReply> request) {
      Promise<io.vertx.howtos.grpcweb.HelloReply> promise = Promise.promise();
      request.handler(msg -> {
        try {
          sayHello(msg, promise);
        } catch (RuntimeException err) {
          promise.tryFail(err);
        }
      });
      promise.future()
        .onFailure(err -> request.response().status(GrpcStatus.INTERNAL).end())
        .onSuccess(resp -> request.response().end(resp));
    }
    public GreeterApi bind_sayHello(GrpcServer server) {
      return bind_sayHello(server, io.vertx.grpc.common.WireFormat.PROTOBUF);
    }
    public GreeterApi bind_sayHello(GrpcServer server, io.vertx.grpc.common.WireFormat format) {
      ServiceMethod<io.vertx.howtos.grpcweb.HelloRequest,io.vertx.howtos.grpcweb.HelloReply> serviceMethod;
      switch(format) {
        case PROTOBUF:
          serviceMethod = SayHello;
          break;
        case JSON:
          serviceMethod = SayHello_JSON;
          break;
        default:
          throw new AssertionError();
      }
      server.callHandler(serviceMethod, this::handle_sayHello);
      return this;
    }

    public final GreeterApi bindAll(GrpcServer server) {
      bind_sayHello(server);
      return this;
    }

    public final GreeterApi bindAll(GrpcServer server, io.vertx.grpc.common.WireFormat format) {
      bind_sayHello(server, format);
      return this;
    }
  }
}
