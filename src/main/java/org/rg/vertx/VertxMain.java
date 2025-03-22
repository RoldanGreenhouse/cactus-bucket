package org.rg.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.rg.vertx.verticles.HttpServerVerticle;
import org.rg.vertx.verticles.util.VerticleUtil;

@Slf4j
public class VertxMain {

    private final Vertx vertx;

    public VertxMain() {
        log.info("Creating...");
        // TODO Read Configs
        VertxOptions options = new VertxOptions();

        log.info("Starting VertX...");
        this.vertx = Vertx.vertx(options)
            .exceptionHandler(error -> log.error("Unhandled exception:", error));

        log.info("Created");
    }

    public void startVerticles() {
        vertx
            .deployVerticle(
                new HttpServerVerticle(),
                VerticleUtil.handleStandardDeployment(HttpServerVerticle.class.getSimpleName())
            );
    }
}
