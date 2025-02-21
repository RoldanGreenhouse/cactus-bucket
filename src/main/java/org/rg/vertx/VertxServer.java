package org.rg.vertx;

import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;
import org.rg.vertx.verticle.web.RestApiVerticle;

import java.time.Duration;

@Slf4j
public class VertxServer extends AbstractVerticle {

    public static void doStart(boolean isDebug) {
        log.info("Loading VertxOptions VertxServer....");
        long blockIntervalMillis = isDebug ? Duration.ofDays(1).toMillis() : 10 * 1000;

        var options = new VertxOptions();
        options.setBlockedThreadCheckInterval(blockIntervalMillis);

        var vertxOptions = options;

        var vertx = Vertx.vertx(vertxOptions);
        vertx.exceptionHandler(error -> log.error("Error raised: ", error));

        var verticleSimpleName = VertxServer.class.getSimpleName();
        vertx.deployVerticle(new VertxServer())
            .onFailure(err -> log.error(
                "Failure raised on deploying Verticle [{}] due to", verticleSimpleName, err
            ))
            .onSuccess(id -> log.info("Deployed {} with id {}", verticleSimpleName, id));
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Starting VertxServer");
        deployVerticles(startPromise);
        log.info("VertxServer started");
        startPromise.complete();
    }

    private Future<String> deployVerticles(Promise<Void> startPromise) {
        var restApiVerticleName = RestApiVerticle.class.getSimpleName();
        log.info("Deploying verticles: ", restApiVerticleName);
        return vertx
            .deployVerticle(new RestApiVerticle())
            .onFailure(error -> {
                log.error("Failure raised on deploying Verticle [{}] due to", restApiVerticleName, error);
                startPromise.fail(error);
            })
            .onSuccess(status -> log.info(
                "Verticle [{}] deployed successfully with id [{}]", restApiVerticleName, status
            ));
    }
}
