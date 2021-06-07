package com.intuit.innersource.reposcanner.commands.fixup;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Exception indicating that the FixupFileTemplates representation was invalid or malformed. For example, if the JSON
 * representation does match the expected schema, or the Map representation contained invalid Objects.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
public class InvalidFixupFileTemplatesException extends RuntimeException {

    private static final long serialVersionUID = 743820441231992377L;
    private final Set<String> validationErrors;

    InvalidFixupFileTemplatesException(final Throwable cause) {
        super(cause);
        validationErrors = ImmutableSet.of();
    }

    InvalidFixupFileTemplatesException(final Collection<String> validationErrors) {
        super(
            validationErrors.stream().collect(Collectors.joining(System.lineSeparator()))
        );
        this.validationErrors = ImmutableSet.copyOf(validationErrors);
    }

    public Set<String> getValidationErrors() {
        return validationErrors;
    }
}
