package com.github.intuit.innersource.reposcanner;

import com.github.intuit.innersource.reposcanner.commands.fixup.FixupFileTemplates;
import com.github.intuit.innersource.reposcanner.commands.fixup.InnerSourceReadinessFixupCommand;
import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport;
import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport.FileRequirementReport;
import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReportCommand;
import com.github.intuit.innersource.reposcanner.loggingservice.LoggingService;
import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.DirectoriesToSearch;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileChecks;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirement;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileToFind;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.RepositoryRequirements;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.rules.ExternalResource;

abstract class BaseTestFixture extends ExternalResource implements CommandTestFixture {

    private static final ImmutableSet<String> BEFORE_PR_SECTION_SYNONYMS = ImmutableSet
        .<String>builder()
        .add("our commitment")
        .add("asking questions")
        .add("contact us")
        .add("where to get help")
        .add("contacts")
        .add("feature requests")
        .add("reporting issues")
        .add("adding functionality")
        .add("bug triage")
        .add("best practices for contributing")
        .add("creating issues")
        .add("adding components")
        .add("frequently asked questions")
        .add("how to submit changes")
        .add("submitting a pull request")
        .add("submitting changes")
        .add("pull request process")
        .add("how to report bug")
        .add("making changes")
        .add("our pledge")
        .add("our standards")
        .add("our responsibilities")
        .add("pull request checklist")
        .add("contribution expectations")
        .add("expectations before contributing")
        .add("expectations before contribution")
        .add("before contribution")
        .add("before contributing")
        .add("before creating pr")
        .build();
    private static final ImmutableSet<String> DURING_PR_SECTION_SYNONYMS = ImmutableSet
        .<String>builder()
        .add("code review expectations")
        .add("code review sla")
        .add("coding conventions")
        .add("style guide")
        .add("branching conventions")
        .add("testing conventions")
        .add("git commit message format")
        .add("updating changelog")
        .add("how to contribute")
        .add("contribution process")
        .add("process for merging a pr")
        .build();
    private static final ImmutableSet<String> AFTER_PR_SECTION_SYNONYMS = ImmutableSet
        .<String>builder()
        .add("want to become a trusted committer?")
        .add("revert policy")
        .add("ownership model")
        .add("support expectations")
        .add("validating changes")
        .add("deployment sla")
        .add("expectations after contributing")
        .add("expectations after contribution")
        .add("expectations after contribution accepted")
        .add("expectations after pr accepted")
        .add("expectations after pr merged")
        .add("after contributing")
        .add("after contribution")
        .add("after pr merged")
        .add("after contribution merged")
        .add("after merge")
        .build();

    public static final FileCheck EMPTY_USAGE_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "USAGE",
        Sets.newHashSet(),
        false,
        true
    );
    public static final FileCheck EMPTY_LOCAL_DEV_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "LOCAL DEVELOPMENT",
        Sets.newHashSet("LOCAL DEV"),
        false,
        true
    );
    public static final FileCheck EMPTY_CONTRIBUTING_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "CONTRIBUTING",
        Sets.newHashSet(),
        false,
        true
    );
    public static final FileCheck EMPTY_SUPPORT_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "SUPPORT",
        Sets.newHashSet(),
        false,
        true
    );
    public static final FileCheck TITLE_CHECK = FileCheck.markdownFileWithTitleHeadingMatching(
        "(?i)^(?!title$).+"
    );
    public static final FileCheck TITLE_DESCRIPTION_CHECK = FileCheck.markdownFileWithDescriptionAfterTitle();
    public static final FileCheck BUILD_STATUS_BADGE_CHECK = FileCheck.markdownFileWithImage(
        "BUILD ICON",
        Sets.newHashSet("BUILD STATUS"),
        false
    );
    public static final FileCheck COVERAGE_BADGE_CHECK = FileCheck.markdownFileWithImage(
        "COVERAGE ICON",
        Sets.newHashSet("CODE COVERAGE"),
        false
    );
    public static final FileCheck NON_EMPTY_USAGE_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "USAGE",
        Sets.newHashSet(),
        false,
        false
    );
    public static final FileCheck NON_EMPTY_LOCAL_DEV_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "LOCAL DEVELOPMENT",
        Sets.newHashSet("LOCAL DEV"),
        false,
        false
    );
    public static final FileCheck NON_EMPTY_CONTRIBUTING_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "CONTRIBUTING",
        Sets.newHashSet(),
        false,
        false
    );
    public static final FileCheck NON_EMPTY_SUPPORT_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "SUPPORT",
        Sets.newHashSet(),
        false,
        false
    );
    public static final FileCheck FILE_EXISTS_CHECK = FileCheck.fileExists();
    public static final FileCheck FILE_NOT_EMPTY_CHECK = FileCheck.fileNotEmpty();
    public static final FileRequirement README_FILE_REQUIREMENT = FileRequirement.create(
        FileToFind.create("/README.md"),
        FileChecks.create(
            FILE_EXISTS_CHECK,
            FILE_NOT_EMPTY_CHECK,
            EMPTY_USAGE_SECTION_CHECK,
            EMPTY_LOCAL_DEV_SECTION_CHECK,
            EMPTY_CONTRIBUTING_SECTION_CHECK,
            EMPTY_SUPPORT_SECTION_CHECK,
            TITLE_CHECK,
            TITLE_DESCRIPTION_CHECK,
            BUILD_STATUS_BADGE_CHECK,
            COVERAGE_BADGE_CHECK,
            NON_EMPTY_USAGE_SECTION_CHECK,
            NON_EMPTY_LOCAL_DEV_SECTION_CHECK,
            NON_EMPTY_CONTRIBUTING_SECTION_CHECK,
            NON_EMPTY_SUPPORT_SECTION_CHECK
        )
    );
    public static final FileCheck FILE_HAS_CODEOWNERS_DEFAULT_RULE_CHECK = FileCheck.fileHasLineMatching(
        "^\\s*\\*\\s+(\\S+@\\S+|@\\S+).*$"
    );
    public static final FileRequirement CODEOWNERS_FILE_REQUIREMENT = FileRequirement.create(
        FileToFind.create("/.github/CODEOWNERS"),
        FileChecks.create(
            FILE_EXISTS_CHECK,
            FILE_NOT_EMPTY_CHECK,
            FILE_HAS_CODEOWNERS_DEFAULT_RULE_CHECK
        )
    );
    public static final FileRequirementOption PULL_REQUEST_TEMPLATE_FILE_OPTION = FileRequirementOption.create(
        FileToFind.create("/.github/PULL_REQUEST_TEMPLATE.md"),
        FileChecks.create(FILE_EXISTS_CHECK, FILE_NOT_EMPTY_CHECK)
    );
    public static final FileCheck DIRECTORY_EXISTS_CHECK = FileCheck.directoryExists();
    public static final FileCheck DIRECTORY_NOT_EMPTY_CHECK = FileCheck.directoryNotEmpty();
    public static final FileRequirementOption PULL_REQUEST_TEMPLATE_DIRECTORY_OPTION = FileRequirementOption.create(
        FileToFind.create("/.github/PULL_REQUEST_TEMPLATE/"),
        FileChecks.create(DIRECTORY_EXISTS_CHECK, DIRECTORY_NOT_EMPTY_CHECK)
    );
    public static final FileRequirement PULL_REQUEST_TEMPLATE_REQUIREMENT = FileRequirement.oneOf(
        PULL_REQUEST_TEMPLATE_FILE_OPTION,
        PULL_REQUEST_TEMPLATE_DIRECTORY_OPTION
    );
    public static final FileCheck EMPTY_BEFORE_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "BEFORE PR",
        BEFORE_PR_SECTION_SYNONYMS,
        false,
        true
    );
    public static final FileCheck EMPTY_DURING_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "DURING PR",
        DURING_PR_SECTION_SYNONYMS,
        false,
        true
    );
    public static final FileCheck EMPTY_AFTER_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "AFTER PR",
        AFTER_PR_SECTION_SYNONYMS,
        false,
        true
    );
    public static final FileCheck NON_EMPTY_BEFORE_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "BEFORE PR",
        BEFORE_PR_SECTION_SYNONYMS,
        false,
        false
    );
    public static final FileCheck NON_EMPTY_DURING_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "DURING PR",
        DURING_PR_SECTION_SYNONYMS,
        false,
        false
    );
    public static final FileCheck NON_EMPTY_AFTER_PR_SECTION_CHECK = FileCheck.markdownFileWithHeading(
        "AFTER PR",
        AFTER_PR_SECTION_SYNONYMS,
        false,
        false
    );
    public static final FileRequirement CONTRIBUTING_FILE_REQUIREMENT = FileRequirement.create(
        FileToFind.create("/CONTRIBUTING.md"),
        FileChecks.create(
            FILE_EXISTS_CHECK,
            FILE_NOT_EMPTY_CHECK,
            EMPTY_BEFORE_PR_SECTION_CHECK,
            EMPTY_DURING_PR_SECTION_CHECK,
            EMPTY_AFTER_PR_SECTION_CHECK,
            NON_EMPTY_BEFORE_PR_SECTION_CHECK,
            NON_EMPTY_DURING_PR_SECTION_CHECK,
            NON_EMPTY_AFTER_PR_SECTION_CHECK
        )
    );
    public static InnerSourceReadinessSpecification SPECIFICATION = InnerSourceReadinessSpecification.create(
        "TestSpec",
        RepositoryRequirements.create(
            DirectoriesToSearch.create("/", "/docs", "/.github"),
            README_FILE_REQUIREMENT,
            CODEOWNERS_FILE_REQUIREMENT,
            PULL_REQUEST_TEMPLATE_REQUIREMENT,
            CONTRIBUTING_FILE_REQUIREMENT
        )
    );

    protected static final ImmutableMap<String, Function<InnerSourceReadinessReport, Boolean>> REPORT_RESULT_LOOKUP_TABLE = ImmutableMap
        .<String, Function<InnerSourceReadinessReport, Boolean>>builder()
        .put(
            "README.md",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(FILE_EXISTS_CHECK) &&
                            fileChecksReport.isFileCheckSatisfied(FILE_NOT_EMPTY_CHECK)
                    )
        )
        .put(
            "README.md Title",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(TITLE_CHECK)
                    )
        )
        .put(
            "README.md Description",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(TITLE_DESCRIPTION_CHECK)
                    )
        )
        .put(
            "README.md Build Status Badges",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                BUILD_STATUS_BADGE_CHECK
                            ) &&
                            fileChecksReport.isFileCheckSatisfied(COVERAGE_BADGE_CHECK)
                    )
        )
        .put(
            "README.md Usage Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_USAGE_SECTION_CHECK
                            )
                    )
        )
        .put(
            "README.md Local Development Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_LOCAL_DEV_SECTION_CHECK
                            )
                    )
        )
        .put(
            "README.md Contributing Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_CONTRIBUTING_SECTION_CHECK
                            )
                    )
        )
        .put(
            "README.md Support Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(README_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_SUPPORT_SECTION_CHECK
                            )
                    )
        )
        .put(
            "CODEOWNERS",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CODEOWNERS_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(FILE_EXISTS_CHECK) &&
                            fileChecksReport.isFileCheckSatisfied(FILE_NOT_EMPTY_CHECK)
                    )
        )
        .put(
            "CODEOWNERS Default Rule",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CODEOWNERS_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                FILE_HAS_CODEOWNERS_DEFAULT_RULE_CHECK
                            )
                    )
        )
        .put(
            "CONTRIBUTING.md",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CONTRIBUTING_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(FILE_EXISTS_CHECK) &&
                            fileChecksReport.isFileCheckSatisfied(FILE_NOT_EMPTY_CHECK)
                    )
        )
        .put(
            "CONTRIBUTING.md Before PR Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CONTRIBUTING_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_BEFORE_PR_SECTION_CHECK
                            )
                    )
        )
        .put(
            "CONTRIBUTING.md During PR Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CONTRIBUTING_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_DURING_PR_SECTION_CHECK
                            )
                    )
        )
        .put(
            "CONTRIBUTING.md After PR Section",
            report ->
                report
                    .getOnlyFileRequirementReportFor(CONTRIBUTING_FILE_REQUIREMENT)
                    .get()
                    .getFileChecksReports()
                    .stream()
                    .anyMatch(
                        fileChecksReport ->
                            fileChecksReport.isFileCheckSatisfied(
                                NON_EMPTY_AFTER_PR_SECTION_CHECK
                            )
                    )
        )
        .put(
            "PULL_REQUEST_TEMPLATE.md",
            report ->
                report
                    .getFileRequirementReportsFor(PULL_REQUEST_TEMPLATE_REQUIREMENT)
                    .stream()
                    .map(FileRequirementReport::getFileChecksReports)
                    .flatMap(List::stream)
                    .anyMatch(
                        fileChecksReport ->
                            (
                                fileChecksReport.isFileCheckSatisfied(
                                    FILE_EXISTS_CHECK
                                ) &&
                                fileChecksReport.isFileCheckSatisfied(
                                    FILE_NOT_EMPTY_CHECK
                                )
                            ) ||
                            (
                                fileChecksReport.isFileCheckSatisfied(
                                    DIRECTORY_EXISTS_CHECK
                                ) &&
                                fileChecksReport.isFileCheckSatisfied(
                                    DIRECTORY_NOT_EMPTY_CHECK
                                )
                            )
                    )
        )
        .build();

    protected final Function<RepositoryFilePath, InnerSourceReadinessReportCommand.Builder> reportCommandBuilderProvider =
        InnerSourceReadinessReportCommand::create;
    protected final Function<RepositoryFilePath, InnerSourceReadinessFixupCommand.Builder> fixupCommandBuilderProvider =
        InnerSourceReadinessFixupCommand::create;

    protected InnerSourceReadinessReport readinessReport;

    public static FixupFileTemplates fixupEmptyFileTemplates = FixupFileTemplates.from(
        ImmutableMap.of(
            "/.github/PULL_REQUEST_TEMPLATE.md",
            "# Describe Proposed Changes",
            "/.github/CODEOWNERS",
            "# This is a comment.\n" +
            "# Each line is a file pattern followed by one or more owners.\n" +
            "\n" +
            "# These owners will be the default owners for everything in\n" +
            "# the repo. Unless a later match takes precedence,\n" +
            "# @global-owner1 and @global-owner2 will be requested for\n" +
            "# review when someone opens a pull request.\n" +
            "# *       @global-owner1 @global-owner2\n" +
            "\n" +
            "# Order is important; the last matching pattern takes the most\n" +
            "# precedence. When someone opens a pull request that only\n" +
            "# modifies JS files, only @js-owner and not the global\n" +
            "# owner(s) will be requested for a review.\n" +
            "# *.js    @js-owner\n" +
            "\n" +
            "# You can also use email addresses if you prefer. They'll be\n" +
            "# used to look up users just like we do for commit author\n" +
            "# emails.\n" +
            "# *.go docs@example.com\n" +
            "\n" +
            "# In this example, @doctocat owns any files in the build/logs\n" +
            "# directory at the root of the repository and any of its\n" +
            "# subdirectories.\n" +
            "# /build/logs/ @doctocat\n" +
            "\n" +
            "# The `docs/*` pattern will match files like\n" +
            "# `docs/getting-started.md` but not further nested files like\n" +
            "# `docs/build-app/troubleshooting.md`.\n" +
            "# docs/*  docs@example.com\n" +
            "\n" +
            "# In this example, @octocat owns any file in an apps directory\n" +
            "# anywhere in your repository.\n" +
            "# apps/ @octocat\n" +
            "\n" +
            "# In this example, @doctocat owns any file in the `/docs`\n" +
            "# directory in the root of your repository and any of its\n" +
            "# subdirectories.\n" +
            "# /docs/ @doctocat"
        )
    );
    protected List<RepositoryFilePath> fixedFiles;
    protected List<String> logOutput;
    protected LoggingService loggingService;
    protected Exception thrownException;

    @Override
    protected void before() throws Throwable {
        readinessReport = null;
        logOutput = Lists.newArrayList();
        loggingService =
            new LoggingService() {
                @Override
                public void info(final String message) {
                    logOutput.add("INFO: " + message);
                }

                @Override
                public void debug(final String message) {
                    logOutput.add("DEBUG: " + message);
                }

                @Override
                public void trace(final String message) {
                    logOutput.add("TRACE: " + message);
                }

                @Override
                public void warn(final String warning) {
                    logOutput.add("WARN: " + warning);
                }

                @Override
                public void error(final String error) {
                    logOutput.add("ERROR: " + error);
                }
            };
        thrownException = null;
    }

    @Override
    protected void after() {}

    @Override
    public void given_logging_service(final LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public void then_readiness_report_matches(final Object expectedReadinessReport) {
        if (expectedReadinessReport == null) {
            Assertions.assertThat(readinessReport).isNull();
            return;
        }

        if (
            (expectedReadinessReport instanceof Class) &&
            Exception.class.isAssignableFrom((Class) expectedReadinessReport)
        ) {
            Assertions
                .assertThat(thrownException)
                .isInstanceOf((Class) expectedReadinessReport);
            return;
        }

        Assertions.assertThat(thrownException).isNull();

        Assertions.assertThat(readinessReport).isNotNull();

        final List<Cell<Function<InnerSourceReadinessReport, Boolean>, Boolean, String>> expectations = StreamSupport
            .stream(
                Splitter
                    .on("\n")
                    .omitEmptyStrings()
                    .split((String) expectedReadinessReport)
                    .spliterator(),
                false
            )
            .map(
                line ->
                    Maps.immutableEntry(
                        StringUtils.substringBefore(line, ".."),
                        StringUtils.substringAfterLast(line, "..")
                    )
            )
            .map(
                entry ->
                    Tables.immutableCell(
                        REPORT_RESULT_LOOKUP_TABLE.get(entry.getKey()),
                        "FOUND".equals(entry.getValue()),
                        String.format("check %s requirement is satisfied", entry.getKey())
                    )
            )
            .collect(Collectors.toList());

        for (final Cell<Function<InnerSourceReadinessReport, Boolean>, Boolean, String> expectation : expectations) {
            final boolean actual = expectation.getRowKey().apply(readinessReport);
            final Boolean expected = expectation.getColumnKey();
            final String description = expectation.getValue();

            Assertions.assertThat(actual).as(description).isEqualTo(expected);
        }
    }

    @Override
    public void then_fixed_files_match(final Object... expected) {
        if ((expected.length == 1) && (expected[0] instanceof List)) {
            Assertions
                .assertThat(this.fixedFiles)
                .containsAll((List<RepositoryFilePath>) expected[0]);
            return;
        }

        if (
            (expected.length == 1) &&
            (expected[0] instanceof Class) &&
            Exception.class.isAssignableFrom((Class) expected[0])
        ) {
            Assertions.assertThat(thrownException).isInstanceOf((Class) expected[0]);
            return;
        }

        final Map<String, String> actualCreatedFilePathsToFileContents =
            this.fixedFiles.stream()
                .map(
                    createdFile -> {
                        final String contents;
                        try {
                            contents =
                                IOUtils.toString(
                                    createdFile.read(),
                                    StandardCharsets.UTF_8
                                );
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        return Maps.immutableEntry(
                            createdFile.toFilePathString(),
                            contents
                        );
                    }
                )
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        final Map<String, String> expectedFilePathsToFileContents = Arrays
            .stream(expected)
            .map(o -> (Supplier<Entry<String, Object>>) o)
            .map(Supplier::get)
            .map(
                entry -> {
                    if (entry.getValue() == null) {
                        return Maps.immutableEntry(entry.getKey(), "");
                    } else if (entry.getValue() instanceof String) {
                        return Maps.immutableEntry(
                            entry.getKey(),
                            (String) entry.getValue()
                        );
                    } else {
                        try {
                            final URL classpathUrl = (URL) entry.getValue();
                            return Maps.immutableEntry(
                                entry.getKey(),
                                IOUtils.toString(classpathUrl, StandardCharsets.UTF_8)
                            );
                        } catch (final IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                }
            )
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        Assertions
            .assertThat(actualCreatedFilePathsToFileContents)
            .isEqualTo(expectedFilePathsToFileContents);
    }

    @Override
    public void then_log_contains_lines(final String... expectedScanLogLines) {
        Assertions
            .assertThat(
                Lists.newArrayList(
                    Splitter.on("\n").split(Joiner.on("\n").join(logOutput))
                )
            )
            .containsAll(Arrays.asList(expectedScanLogLines));
    }

    @Override
    public void then_log_warning_count_is(final int expectedWarningCount) {
        final int warningCount = Ints.saturatedCast(
            StreamSupport
                .stream(
                    Splitter
                        .on("\n")
                        .split(Joiner.on("\n").join(logOutput))
                        .spliterator(),
                    false
                )
                .filter(line -> line.startsWith("WARN:"))
                .count()
        );

        Assertions
            .assertThat(warningCount)
            .as("check scan log warning count")
            .isEqualTo(expectedWarningCount);
    }
}
