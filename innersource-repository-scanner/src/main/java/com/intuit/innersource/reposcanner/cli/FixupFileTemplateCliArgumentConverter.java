package com.intuit.innersource.reposcanner.cli;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.intuit.innersource.reposcanner.commands.fixup.FixupFileTemplates;
import com.intuit.innersource.reposcanner.commands.fixup.InvalidFixupFileTemplatesException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class FixupFileTemplateCliArgumentConverter
    implements ITypeConverter<FixupFileTemplates> {

    @Override
    public FixupFileTemplates convert(final String fileTemplatesJsonFilePath)
        throws Exception {
        try {
            return FixupFileTemplates.fromJson(
                Files
                    .asCharSource(
                        Paths.get(fileTemplatesJsonFilePath).toFile(),
                        StandardCharsets.UTF_8
                    )
                    .read()
            );
        } catch (final InvalidFixupFileTemplatesException cause) {
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
