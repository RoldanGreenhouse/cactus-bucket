package org.rg.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.rg.vertx.verticles.util.VerticleUtil;
import org.rg.vertx.verticles.web.HealthCheckVerticle;

@Slf4j
public class HttpServerVerticle extends AbstractVerticle {

    private final HttpServerOptions httpOptions;
    @Getter private final Router router;

    public HttpServerVerticle() {
        log.info("Creating...");
        this.router = Router.router(vertx);
        this.httpOptions = new HttpServerOptions()
            .setPort(8080);
        log.info("Created");
    }

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Starting...");
        getVertx()
            .createHttpServer(this.httpOptions)
            .requestHandler(this.router)
            .listen(httpServer -> {
                if (httpServer.succeeded()) {
                    log.info("Started. Listening to port [{}]", this.httpOptions.getPort());
                    deployWebVerticles(startPromise);
                    return;
                }
                Throwable error = httpServer.cause();
                log.error("Unable to start due to:", error);
                startPromise.fail(error);
            });
    }

    private void deployWebVerticles(Promise<Void> startPromise) {
        log.info("Deploying Web related Verticles....");
        getVertx()
            .deployVerticle(
                new HealthCheckVerticle(this.router),
                VerticleUtil.handleStandardDeployment(HealthCheckVerticle.class.getSimpleName(), startPromise)
            );
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        log.info("Stopping...");
        stopPromise.complete();
        log.info("Stopped.");
    }
}
