package com.intuit.innersource.reposcanner.specification;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception indicating that the InnerSource Readiness Specification representation was invalid or malformed. For
 * example if the JSON representation did not match the expected schema.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
public class InvalidInnerSourceReadinessSpecificationException extends RuntimeException {

    private static final long serialVersionUID = -3173381703157604462L;
    private final Set<String> validationErrors;

    InvalidInnerSourceReadinessSpecificationException(final Throwable cause) {
        super(cause);
        validationErrors = ImmutableSet.of();
    }

    InvalidInnerSourceReadinessSpecificationException(
        final Collection<String> validationErrors
    ) {
        super(
            validationErrors.stream().collect(Collectors.joining(System.lineSeparator()))
        );
        this.validationErrors = ImmutableSet.copyOf(validationErrors);
    }

    public Set<String> getValidationErrors() {
        return validationErrors;
    }
}
