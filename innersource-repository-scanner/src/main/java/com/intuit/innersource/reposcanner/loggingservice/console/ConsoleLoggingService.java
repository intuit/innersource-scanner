package com.intuit.innersource.reposcanner.loggingservice.console;

import com.intuit.innersource.reposcanner.loggingservice.LoggingService;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public final class ConsoleLoggingService implements LoggingService {

    public static final LoggingService INSTANCE = new ConsoleLoggingService();

    private ConsoleLoggingService() {}

    @Override
    public void info(final String message) {
        System.out.println("INFO: " + message);
    }

    @Override
    public void debug(final String message) {
        System.out.println("DEBUG: " + message);
    }

    @Override
    public void trace(final String message) {
        System.out.println("TRACE: " + message);
    }

    @Override
    public void warn(final String warning) {
        System.err.println("WARN: " + warning);
    }

    @Override
    public void error(final String error) {
        System.err.println("ERROR: " + error);
    }
}
