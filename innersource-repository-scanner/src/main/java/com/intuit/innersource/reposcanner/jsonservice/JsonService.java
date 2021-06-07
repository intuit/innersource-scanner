package com.intuit.innersource.reposcanner.jsonservice;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.intuit.innersource.reposcanner.commands.fixup.GsonAdaptersFixupFileTemplates;
import com.intuit.innersource.reposcanner.commands.report.GsonAdaptersInnerSourceReadinessReport;
import com.intuit.innersource.reposcanner.jsonservice.ImmutableJsonService;
import com.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.intuit.innersource.reposcanner.specification.GsonAdaptersInnerSourceReadinessSpecification;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.leadpony.justify.api.JsonValidationService;

@Immutable(singleton = true, builder = false)
@Style(visibility = ImplementationVisibility.PACKAGE)
public abstract class JsonService {

    JsonService() {}

    public static JsonService getInstance() {
        return ImmutableJsonService.of();
    }

    @Lazy
    Gson gson() {
        return new GsonBuilder()
            .registerTypeAdapter(
                RepositoryFilePath.class,
                (JsonSerializer<RepositoryFilePath>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toFilePathString())
            )
            .registerTypeAdapterFactory(
                new GsonAdaptersInnerSourceReadinessSpecification()
            )
            .registerTypeAdapterFactory(new GsonAdaptersFixupFileTemplates())
            .registerTypeAdapterFactory(new GsonAdaptersInnerSourceReadinessReport())
            .setPrettyPrinting()
            .create();
    }

    public String toJson(final Object src) {
        return gson().toJson(src);
    }

    public <T> T fromJson(final String json, final Class<T> clazz) {
        return gson().fromJson(json, clazz);
    }

    public Set<String> validateJsonAgainstClasspathSchema(
        final String json,
        final String schemaClasspathLocation
    ) throws IOException {
        final Set<String> validationErrors = Sets.newHashSet();
        final JsonValidationService validationService = JsonValidationService.newInstance();
        final JsonParserFactory parserFactory = validationService.createParserFactory(
            validationService
                .createValidationConfig()
                .withSchema(
                    validationService.readSchema(
                        JsonService.class.getResourceAsStream(schemaClasspathLocation),
                        StandardCharsets.UTF_8
                    )
                )
                .withDefaultValues(true)
                .withProblemHandler(
                    validationService.createProblemPrinter(validationErrors::add)
                )
                .getAsMap()
        );
        try (final Reader br = new StringReader(json)) {
            try (final JsonParser jsonParser = parserFactory.createParser(br)) {
                while (jsonParser.hasNext()) {
                    jsonParser.next();
                }
            }
        }
        return validationErrors;
    }
}
