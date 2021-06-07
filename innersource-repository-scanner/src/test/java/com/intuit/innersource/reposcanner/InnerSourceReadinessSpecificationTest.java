package com.intuit.innersource.reposcanner;

import com.google.common.collect.Sets;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.DirectoriesToSearch;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileChecks;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirement;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileToFind;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.RepositoryRequirements;
import com.intuit.innersource.reposcanner.specification.InvalidInnerSourceReadinessSpecificationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Enclosed.class)
public class InnerSourceReadinessSpecificationTest {

    @RunWith(Parameterized.class)
    public static class SpecificationToJsonTests {

        @Parameters(name = "{index} - {0}")
        public static Collection<Object[]> toJsonValidationTests() {
            return Arrays.asList(
                new Object[][] {
                    {
                        "given spec with file exists requirement when toJson then matches expected json",
                        InnerSourceReadinessSpecification.create(
                            "specName",
                            RepositoryRequirements.create(
                                DirectoriesToSearch.create("/", "/docs", "/.github"),
                                FileRequirement.create(
                                    FileToFind.create("/README.md"),
                                    FileChecks.create(FileCheck.fileExists())
                                )
                            )
                        ),
                        "{\n" +
                        "  \"specName\": \"specName\",\n" +
                        "  \"repositoryRequirements\": {\n" +
                        "    \"directoriesToSearch\": {\n" +
                        "      \"directoryPaths\": [\n" +
                        "        \"/\",\n" +
                        "        \"/docs\",\n" +
                        "        \"/.github\"\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    \"requiredFiles\": [\n" +
                        "      {\n" +
                        "        \"requiredFileOptions\": [\n" +
                        "          {\n" +
                        "            \"fileToFind\": {\n" +
                        "              \"expectedFilePath\": \"/README.md\"\n" +
                        "            },\n" +
                        "            \"fileChecks\": {\n" +
                        "              \"checks\": [\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_EXISTS\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}",
                    },
                    {
                        "given spec with two options for readme and 1 option for contributing when toJson then matches expected json",
                        InnerSourceReadinessSpecification.create(
                            "specName",
                            RepositoryRequirements.create(
                                DirectoriesToSearch.create("/", "/docs", "/.github"),
                                FileRequirement.oneOf(
                                    FileRequirementOption.create(
                                        FileToFind.create("/README.md"),
                                        FileChecks.create(
                                            FileCheck.fileExists(),
                                            FileCheck.fileNotEmpty(),
                                            FileCheck.directoryExists(),
                                            FileCheck.directoryNotEmpty(),
                                            FileCheck.fileHasLineMatching("someLine.+"),
                                            FileCheck.markdownFileWithTitleHeadingMatching(
                                                "title.+"
                                            ),
                                            FileCheck.markdownFileWithHeading(
                                                "Heading",
                                                Sets.newHashSet("Synonym1", "Synonym2"),
                                                true,
                                                true
                                            ),
                                            FileCheck.markdownFileWithDescriptionAfterTitle(),
                                            FileCheck.markdownFileWithImage(
                                                "AltText",
                                                Sets.newHashSet("Synonym1", "Synonym2"),
                                                true
                                            )
                                        )
                                    ),
                                    FileRequirementOption.create(
                                        FileToFind.create("/.github/README.md"),
                                        FileChecks.create(FileCheck.fileExists())
                                    )
                                ),
                                FileRequirement.create(
                                    FileToFind.create("/CONTRIBUTING.md"),
                                    FileChecks.create(
                                        FileCheck.fileExists(),
                                        FileCheck.fileNotEmpty(),
                                        FileCheck.directoryExists(),
                                        FileCheck.directoryNotEmpty(),
                                        FileCheck.fileHasLineMatching("someLine.+"),
                                        FileCheck.markdownFileWithTitleHeadingMatching(
                                            "title.+"
                                        ),
                                        FileCheck.markdownFileWithHeading(
                                            "Heading",
                                            Sets.newHashSet("Synonym1", "Synonym2"),
                                            true,
                                            true
                                        ),
                                        FileCheck.markdownFileWithDescriptionAfterTitle(),
                                        FileCheck.markdownFileWithImage(
                                            "AltText",
                                            Sets.newHashSet("Synonym1", "Synonym2"),
                                            true
                                        )
                                    )
                                )
                            )
                        ),
                        "{\n" +
                        "  \"specName\": \"specName\",\n" +
                        "  \"repositoryRequirements\": {\n" +
                        "    \"directoriesToSearch\": {\n" +
                        "      \"directoryPaths\": [\n" +
                        "        \"/\",\n" +
                        "        \"/docs\",\n" +
                        "        \"/.github\"\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    \"requiredFiles\": [\n" +
                        "      {\n" +
                        "        \"requiredFileOptions\": [\n" +
                        "          {\n" +
                        "            \"fileToFind\": {\n" +
                        "              \"expectedFilePath\": \"/README.md\"\n" +
                        "            },\n" +
                        "            \"fileChecks\": {\n" +
                        "              \"checks\": [\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_EXISTS\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_NOT_EMPTY\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"DIRECTORY_EXISTS\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"DIRECTORY_NOT_EMPTY\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_HAS_LINE_MATCHING\",\n" +
                        "                  \"regexPattern\": \"someLine.+\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_TITLE_HEADING\",\n" +
                        "                  \"titleRegexPattern\": \"title.+\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_HEADING\",\n" +
                        "                  \"heading\": \"Heading\",\n" +
                        "                  \"synonyms\": [\n" +
                        "                    \"Synonym1\",\n" +
                        "                    \"Synonym2\"\n" +
                        "                  ],\n" +
                        "                  \"matchCase\": true,\n" +
                        "                  \"matchIfSectionEmpty\": true\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_IMAGE\",\n" +
                        "                  \"altText\": \"AltText\",\n" +
                        "                  \"altTextSynonyms\": [\n" +
                        "                    \"Synonym1\",\n" +
                        "                    \"Synonym2\"\n" +
                        "                  ],\n" +
                        "                  \"matchCase\": true\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"fileToFind\": {\n" +
                        "              \"expectedFilePath\": \"/.github/README.md\"\n" +
                        "            },\n" +
                        "            \"fileChecks\": {\n" +
                        "              \"checks\": [\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_EXISTS\"\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"requiredFileOptions\": [\n" +
                        "          {\n" +
                        "            \"fileToFind\": {\n" +
                        "              \"expectedFilePath\": \"/CONTRIBUTING.md\"\n" +
                        "            },\n" +
                        "            \"fileChecks\": {\n" +
                        "              \"checks\": [\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_EXISTS\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_NOT_EMPTY\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"DIRECTORY_EXISTS\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"DIRECTORY_NOT_EMPTY\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"FILE_HAS_LINE_MATCHING\",\n" +
                        "                  \"regexPattern\": \"someLine.+\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_TITLE_HEADING\",\n" +
                        "                  \"titleRegexPattern\": \"title.+\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_HEADING\",\n" +
                        "                  \"heading\": \"Heading\",\n" +
                        "                  \"synonyms\": [\n" +
                        "                    \"Synonym1\",\n" +
                        "                    \"Synonym2\"\n" +
                        "                  ],\n" +
                        "                  \"matchCase\": true,\n" +
                        "                  \"matchIfSectionEmpty\": true\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE\"\n" +
                        "                },\n" +
                        "                {\n" +
                        "                  \"requirement\": \"MARKDOWN_FILE_HAS_IMAGE\",\n" +
                        "                  \"altText\": \"AltText\",\n" +
                        "                  \"altTextSynonyms\": [\n" +
                        "                    \"Synonym1\",\n" +
                        "                    \"Synonym2\"\n" +
                        "                  ],\n" +
                        "                  \"matchCase\": true\n" +
                        "                }\n" +
                        "              ]\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}",
                    },
                }
            );
        }

        @Parameter(0)
        public String testDescription;

        @Parameter(1)
        public InnerSourceReadinessSpecification specification;

        @Parameter(2)
        public String expectedJson;

        @Test
        public void testSpecToJson() {
            Assertions.assertThat(specification.toJson()).isEqualTo(expectedJson);
        }
    }

    @RunWith(Parameterized.class)
    public static class FromJsonSpecificationTests {

        @Parameters(name = "{index} - {0}")
        public static Collection<Object[]> fromJsonValidationTests() {
            return Arrays.asList(
                new Object[][] {
                    {
                        "given json spec without specName and repositoryRequirements properties when fromJson then missing property validation errors",
                        "{}",
                        Sets.newHashSet(
                            "must have a property whose name is \"specName\"",
                            "must have a property whose name is \"repositoryRequirements\""
                        ),
                    },
                    {
                        "given json spec with invalid specName and missing repositoryRequirements when fromJson then wrong type and missing property validation errors",
                        "{ \"specName\": [] }",
                        Sets.newHashSet(
                            "value must be of string type, but actual type is array",
                            "must have a property whose name is \"repositoryRequirements\""
                        ),
                    },
                    {
                        "given json spec with invalid repositoryRequirements when fromJson then wrong type validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": [] " +
                        "}",
                        Sets.newHashSet(
                            "value must be of object type, but actual type is array"
                        ),
                    },
                    {
                        "given json spec with empty repositoryRequirements when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"directoriesToSearch\"",
                            "must have a property whose name is \"requiredFiles\""
                        ),
                    },
                    {
                        "given json spec with empty directoriesToSearch when fromJson then missing property validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {} " +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"directoryPaths\"",
                            "must have a property whose name is \"requiredFiles\""
                        ),
                    },
                    {
                        "given json spec with empty directoryPaths when fromJson then insufficient elements error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": []" +
                        "} " +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have at least 1 element",
                            "must have a property whose name is \"requiredFiles\""
                        ),
                    },
                    {
                        "given json spec with invalid directoryPaths when fromJson then wrong type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": { " +
                        "\"directoryPaths\": [" +
                        "42" +
                        "]} " +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of string type, but actual type is integer",
                            "must have a property whose name is \"requiredFiles\""
                        ),
                    },
                    {
                        "given json spec missing requiredFiles when fromJson then missing property validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]} " +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"requiredFiles\""
                        ),
                    },
                    {
                        "given json spec with invalid requiredFiles when fromJson then wrong type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": \"requiredFiles\"" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "value must be of array type, but actual type is string"
                        ),
                    },
                    {
                        "given json spec with empty requiredFiles when fromJson then insufficient elements validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": []" +
                        "} " +
                        "}",
                        Sets.newHashSet("array must have at least 1 element"),
                    },
                    {
                        "given json spec with invalid requiredFile when fromJson then wrong type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "42" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "value must be of object type, but actual type is integer"
                        ),
                    },
                    {
                        "given json spec with requiredFile missing properties when fromJson then missing properties validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"requiredFileOptions\""
                        ),
                    },
                    {
                        "given json spec with invalid requiredFileOptions when fromJson then wrong type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": 42 " +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of array type, but actual type is integer"
                        ),
                    },
                    {
                        "given json spec with empty requiredFileOptions when fromJson then missing element validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [] " +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet("must have at least 1 element"),
                    },
                    {
                        "given json spec with empty requiredFileOption when fromJson then missing properties validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"fileToFind\"",
                            "must have a property whose name is \"fileChecks\""
                        ),
                    },
                    {
                        "given json spec with invalid fileToFind when fromJson then invalid type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": []" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of object type, but actual type is array",
                            "must have a property whose name is \"fileChecks\""
                        ),
                    },
                    {
                        "given json spec with empty fileToFind when fromJson then missing property validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"expectedFilePath\"",
                            "must have a property whose name is \"fileChecks\""
                        ),
                    },
                    {
                        "given json spec with invalid expectedFilePath when fromJson then invalid type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": []" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of string type, but actual type is array",
                            "must have a property whose name is \"fileChecks\""
                        ),
                    },
                    {
                        "given json spec with invalid fileChecks when fromJson then wrong type property validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": []" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of object type, but actual type is array"
                        ),
                    },
                    {
                        "given json spec with empty fileChecks when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet("must have a property whose name is \"checks\""),
                    },
                    {
                        "given json spec with invalid checks when fromJson then invalid type validation error",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": 42" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of array type, but actual type is integer"
                        ),
                    },
                    {
                        "given json spec with empty checks when fromJson then no insufficient elements validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": []" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet("must have at least 1 element"),
                    },
                    {
                        "given json spec with invalid fileCheck when fromJson then invalid type validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [42]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of object type, but actual type is integer"
                        ),
                    },
                    {
                        "given json spec with empty fileCheck when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"fileChecks\"",
                            "must have a property whose name is \"propertyNames\"",
                            "must have a property whose name is \"requirement\"",
                            "must have a property whose name is \"regexPattern\"",
                            "must have a property whose name is \"heading\"",
                            "must have a property whose name is \"titleRegexPattern\"",
                            "must have a property whose name is \"altText\""
                        ),
                    },
                    {
                        "given json spec with invalid requirement when fromJson then wrong type property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{" +
                        "\"requirement\": []" +
                        "}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must be of string type, but actual type is array"
                        ),
                    },
                    {
                        "given json spec with FILE_HAS_LINE_MATCHING requirement and missing regexPattern when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{" +
                        "\"requirement\": \"FILE_HAS_LINE_MATCHING\"" +
                        "}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"regexPattern\""
                        ),
                    },
                    {
                        "given json spec with MARKDOWN_HAS_TITLE_HEADING requirement and no regex when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{" +
                        "\"requirement\": \"MARKDOWN_FILE_HAS_TITLE_HEADING\"" +
                        "}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet(
                            "must have a property whose name is \"titleRegexPattern\""
                        ),
                    },
                    {
                        "given json spec with MARKDOWN_FILE_HAS_HEADING requirement and no heading when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{" +
                        "\"requirement\": \"MARKDOWN_FILE_HAS_HEADING\"" +
                        "}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet("must have a property whose name is \"heading\""),
                    },
                    {
                        "given json spec with MARKDOWN_FILE_HAS_IMAGE requirement and altText when fromJson then missing property validation errors",
                        "{ " +
                        "\"specName\": \"specName\", " +
                        "\"repositoryRequirements\": {" +
                        "\"directoriesToSearch\": {" +
                        "\"directoryPaths\": [" +
                        "\"/\"," +
                        "\"/docs\"," +
                        "\"/.github\"" +
                        "]}, " +
                        "\"requiredFiles\": [" +
                        "{" +
                        "\"requiredFileOptions\": [{" +
                        "\"fileToFind\": {" +
                        "\"expectedFilePath\": \"/README.md\"" +
                        "}," +
                        "\"fileChecks\": {" +
                        "\"checks\": [{" +
                        "\"requirement\": \"MARKDOWN_FILE_HAS_IMAGE\"" +
                        "}]" +
                        "}" +
                        "}]" +
                        "}" +
                        "]" +
                        "} " +
                        "}",
                        Sets.newHashSet("must have a property whose name is \"altText\""),
                    },
                }
            );
        }

        @Parameter(0)
        public String testDescription;

        @Parameter(1)
        public String jsonSpec;

        @Parameter(2)
        public Set<String> expectedValidationErrors;

        @Test
        public void testSpecFromJson() {
            try {
                InnerSourceReadinessSpecification.fromJson(jsonSpec);
                Assertions.assertThat(expectedValidationErrors).isEmpty();
            } catch (final InvalidInnerSourceReadinessSpecificationException e) {
                Assertions
                    .assertThat(e.getValidationErrors())
                    .hasSize(expectedValidationErrors.size());
                for (final String expectedValidationError : expectedValidationErrors) {
                    Assertions
                        .assertThat(e.getValidationErrors())
                        .anySatisfy(
                            validationError ->
                                Assertions
                                    .assertThat(validationError)
                                    .contains(expectedValidationError)
                        );
                }
            }
        }
    }
}
