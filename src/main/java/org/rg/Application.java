package org.rg;

import lombok.extern.slf4j.Slf4j;
import org.rg.vertx.VertxServer;

@Slf4j
public class Application {
    public static void main(String[] args) throws Exception {
        log.info("Starting application");
        VertxServer.doStart(true);
        log.info("Application started");
    }
}