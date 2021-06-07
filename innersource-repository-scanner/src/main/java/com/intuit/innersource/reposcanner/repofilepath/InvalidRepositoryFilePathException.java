package com.intuit.innersource.reposcanner.repofilepath;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public final class InvalidRepositoryFilePathException extends RuntimeException {

    private static final long serialVersionUID = -2210401710913142821L;

    public InvalidRepositoryFilePathException(final String message) {
        super(message);
    }
}
