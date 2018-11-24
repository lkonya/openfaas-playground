package openfaas.vertx

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val port = 9000
    try {
      vertx
              .createHttpServer()
              .requestHandler { req ->
                req.response()
                        .putHeader("content-type", "text/plain")
                        .end("Hello from Vert.x!")
              }
              .listen(port) { http ->
                if (http.succeeded()) {
                  startFuture.complete()
                  println("HTTP server started on port $port")
                } else {
                  startFuture.fail(http.cause())
                }
              }
    } catch (e: Exception) {
      println("HTTP server failed to start on port $port, ${e.message}")
    }
  }
}
