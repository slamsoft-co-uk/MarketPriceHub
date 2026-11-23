package com.mizuho.external.mph.services;

import org.slf4j.Logger;

import java.util.Locale;

final class ServiceExecutionLogger {

    private ServiceExecutionLogger() {}

    static void logCompletion(Logger logger, String operation, long startTime, boolean completed) {
        String durationSeconds = String.format(Locale.ROOT, "%.4f",
                (System.nanoTime() - startTime) / 1_000_000_000.0);
        logger.info("Service operation {} finished: outcome={}, durationSeconds={}",
                operation, completed ? "success" : "failure", durationSeconds);
    }
}
