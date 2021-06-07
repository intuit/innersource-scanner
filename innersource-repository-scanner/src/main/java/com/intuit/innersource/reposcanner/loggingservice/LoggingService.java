package com.intuit.innersource.reposcanner.loggingservice;

/**
 * A LoggingService is an API which abstracts how and where log messages are written.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
public interface LoggingService {
    void info(final String message);

    void debug(final String message);

    void trace(final String message);

    void warn(final String warning);

    void error(final String error);
}
