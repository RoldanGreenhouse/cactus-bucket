package org.rg.vertx.verticles.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthCheckVerticle extends AbstractVerticle {

    public static final String HEALTH_CHECK_PATH = "/health";

    private final Router router;
    private HealthCheckHandler handler;

    public HealthCheckVerticle(Router router) {
        log.info("Creating...");
        this.router = router;
        log.info("Created");
    }

    @Override
    public void start(Promise<Void> startPromise) {
       log.info("Starting...");
       setUpHandler();
       registerRoutes();
       log.info("Started");
    }

    private void setUpHandler() {
        log.info("Setting up Handlers...");
        this.handler = HealthCheckHandler.create(getVertx());
        this.handler.register(
            HealthCheckVerticle.class.getSimpleName(),
            statusPromise -> {
                statusPromise.complete(Status.OK());
                log.info("Handler HealthCheckHandler complete");
            }
        );
        /**
         * Here should go all the checks to Databases connections or services that could be useful to check
         * More info: https://vertx.io/docs/vertx-health-check/java/#_procedures
         */
    }

    private void registerRoutes() {
        log.info("Setting up the Routes for HealthCheckVerticle.");
        this.router.get(HEALTH_CHECK_PATH).handler(handler);
        log.info("Routes complete");
    }
}
