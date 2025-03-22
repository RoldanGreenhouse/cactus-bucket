package org.rg.vertx.verticles.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerticleUtil {
    public static Handler<AsyncResult<String>> handleStandardDeployment(String simpleClassName) {
        return handleStandardDeployment(simpleClassName, null);
    }

    public static Handler<AsyncResult<String>> handleStandardDeployment(String simpleClassName, Promise<Void> promise) {
        return deployment -> {
            if (deployment.succeeded()) {
                log.info("Verticle [{}] deployment.", simpleClassName);
                log.debug(deployment.result());
                if (promise != null)
                    promise.complete();
                return;
            }
            log.error("Unable to deploy verticle [{}] due to:", simpleClassName, deployment.cause());
        };
    }
}
