package com.intuit.innersource.reposcanner.loggingservice.slf4j;

import com.intuit.innersource.reposcanner.loggingservice.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public final class Slf4jLoggingService implements LoggingService {

    public static final LoggingService INSTANCE = new Slf4jLoggingService();

    private static final Logger logger = LoggerFactory.getLogger(
        Slf4jLoggingService.class
    );

    private Slf4jLoggingService() {}

    @Override
    public void info(final String message) {
        logger.info(message);
    }

    @Override
    public void warn(final String warning) {
        logger.warn(warning);
    }

    @Override
    public void debug(final String message) {
        logger.debug(message);
    }

    @Override
    public void trace(final String message) {
        logger.trace(message);
    }

    @Override
    public void error(final String error) {
        logger.error(error);
    }
}
