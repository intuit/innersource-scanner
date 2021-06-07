package com.intuit.innersource.reposcanner.cli;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.intuit.innersource.reposcanner.specification.InvalidInnerSourceReadinessSpecificationException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public final class InnerSourceReadinessSpecificationCliArgumentConverter
    implements ITypeConverter<InnerSourceReadinessSpecification> {

    @Override
    public InnerSourceReadinessSpecification convert(final String specFilePath)
        throws Exception {
        try {
            return InnerSourceReadinessSpecification.fromJson(
                Files
                    .asCharSource(
                        Paths.get(specFilePath).toFile(),
                        StandardCharsets.UTF_8
                    )
                    .read()
            );
        } catch (final InvalidInnerSourceReadinessSpecificationException cause) {
            if (cause.getValidationErrors().isEmpty()) {
                throw cause;
            } else {
                throw new TypeConversionException(
                    String.format(
                        "Validation Errors:%s%s",
                        System.lineSeparator(),
                        Joiner
                            .on(System.lineSeparator())
                            .join(cause.getValidationErrors())
                    )
                );
            }
        }
    }
}
