package com.intuit.innersource.reposcanner.loggingservice.noop;

import com.intuit.innersource.reposcanner.loggingservice.LoggingService;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public final class NoopLoggingService implements LoggingService {

    public static final LoggingService INSTANCE = new NoopLoggingService();

    private NoopLoggingService() {}

    @Override
    public void info(final String message) {}

    @Override
    public void debug(final String message) {}

    @Override
    public void trace(final String message) {}

    @Override
    public void warn(final String warning) {}

    @Override
    public void error(final String error) {}
}
