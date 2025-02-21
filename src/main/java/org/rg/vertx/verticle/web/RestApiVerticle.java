package org.rg.vertx.verticle.web;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {

    @Override
    public void start(final Promise<Void> startPromise) {
        log.info("Starting RestApiVerticle...");
        final Router restApi = Router.router(vertx);

        restApi.route().handler(handleDefaultBehavior());
        restApi.route().last().handler(handleRouteNotFound());
        restApi.route().failureHandler(handleFailure());

        deployWebVerticles(restApi);

        startServer(startPromise, restApi);
    }

    private void startServer(Promise<Void> startPromise, Router restApi) {
        log.info("Starting HTTP server....");
        vertx
            .createHttpServer()
            .requestHandler(restApi)
            .exceptionHandler(error -> log.error("HTTP Server error", error))
            .listen(8080, server -> {
                if (server.succeeded()) {
                    log.info("HTTP server started on port {}", server.result().actualPort());
                    startPromise.complete();
                } else {
                    Throwable cause = server.cause();
                    log.error("HTTP server failed to start due to: {}", cause);
                    startPromise.fail(cause);
                }
            });
    }

    private void deployWebVerticles(Router restApi) {
        log.info("Deploying web verticles...");
        vertx.deployVerticle(new HealthCheckVerticle(restApi), status -> {
            var verticleSimpleName = HealthCheckVerticle.class.getSimpleName();
            log.debug("Status [{}]", status);
            if (status.succeeded())
                log.info("Verticle [{}] deployed successfully.", verticleSimpleName);
            else
                log.error("Failure raised on deploying Verticle [{}] due to", verticleSimpleName, status.cause());
        });
    }

    private Handler<RoutingContext> handleDefaultBehavior() {
        return context -> {
            logRequest(context.request());
            addDefaultContentTypeOnResponse(context.response());
            context.next();
        };
    }

    private Handler<RoutingContext> handleRouteNotFound() {
        return context -> {
            JsonObject notFoundResponse = new JsonObject()
                    .put("error", "Endpoint not found")
                    .put("path", context.request().path());

            context.response()
                    .setStatusCode(404)
                    .end(notFoundResponse.encode());
        };
    }

    private Handler<RoutingContext> handleFailure() {
        return errorContext -> {
            if (errorContext.response().ended()) {
                // Ignore completed response
                return;
            }

            log.error("Route Error:", errorContext.failure());
            errorContext.response()
                .setStatusCode(500)
                .end(new JsonObject().put("error", "Something went wrong.").toBuffer());
        };
    }

    private void logRequest(HttpServerRequest request) {
        log.info("Request [{}] received or [{}]", request.method(), request.path());
    }

    private void addDefaultContentTypeOnResponse(HttpServerResponse response) {
        response.putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
    }
}
