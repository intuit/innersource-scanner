package com.intuit.innersource.reposcanner.specification;

import com.google.common.collect.Lists;
import com.intuit.innersource.reposcanner.jsonservice.JsonService;
import com.intuit.innersource.reposcanner.specification.ImmutableDirectoriesToSearch;
import com.intuit.innersource.reposcanner.specification.ImmutableDirectoryContainsFileSatisfyingCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableFileCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableFileChecks;
import com.intuit.innersource.reposcanner.specification.ImmutableFileHasLineMatchingCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableFileHasYamlFrontMatterPropertiesCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableFileRequirement;
import com.intuit.innersource.reposcanner.specification.ImmutableFileRequirementOption;
import com.intuit.innersource.reposcanner.specification.ImmutableFileToFind;
import com.intuit.innersource.reposcanner.specification.ImmutableInnerSourceReadinessSpecification;
import com.intuit.innersource.reposcanner.specification.ImmutableMarkdownFileHasHeadingCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableMarkdownFileHasImageCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableMarkdownFileHasTitleHeadingCheck;
import com.intuit.innersource.reposcanner.specification.ImmutableRepositoryRequirements;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.immutables.gson.Gson;
import org.immutables.value.Value.Derived;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.immutables.value.internal.$processor$.meta.$ValueMirrors.Default;

/**
 * An {@code InnerSourceReadinessSpecification} represents which files are required in a git repository, which
 * directories the files may appear in and a set of checks that each file must pass. A git repository is InnerSource
 * ready if at least one file can be found which satisfies all of the file checks for each of the file requirements.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
@Gson.TypeAdapters(emptyAsNulls = true)
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
@SuppressWarnings("immutables:subtype")
public abstract class InnerSourceReadinessSpecification {

    public static final InnerSourceReadinessSpecification ENTERPRISE_GITHUB_DEFAULT = InnerSourceReadinessSpecification.create(
        "ENTERPRISE_GITHUB_DEFAULT",
        RepositoryRequirements.create(
            DirectoriesToSearch.create("/", "/docs", "/.github"),
            FileRequirement.create(
                FileToFind.create("/README.md"),
                FileChecks.create(
                    FileCheck.fileNotEmpty(),
                    FileCheck.markdownFileWithTitleHeadingMatching("(?i)^(?!title$).*"),
                    FileCheck.markdownFileWithDescriptionAfterTitle()
                )
            ),
            FileRequirement.create(
                FileToFind.create("/CONTRIBUTING.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/SUPPORT.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/.github/CODEOWNERS"),
                FileChecks.create(
                    FileCheck.fileNotEmpty(),
                    FileCheck.fileHasLineMatching("^\\s*\\*\\s+(\\S+@\\S+|@\\S+).*$")
                )
            ),
            FileRequirement.create(
                FileToFind.create("/.github/ISSUE_TEMPLATE/"),
                FileChecks.create(
                    FileCheck.directoryNotEmpty(),
                    FileCheck.directoryContainsFileSatisfying(
                        FileChecks.create(
                            FileCheck.fileHasYamlFrontMatterProperties("name", "about")
                        )
                    )
                )
            ),
            FileRequirement.oneOf(
                FileRequirementOption.create(
                    FileToFind.create("/.github/PULL_REQUEST_TEMPLATE/"),
                    FileChecks.create(FileCheck.directoryNotEmpty())
                ),
                FileRequirementOption.create(
                    FileToFind.create("/.github/PULL_REQUEST_TEMPLATE.md"),
                    FileChecks.create(FileCheck.fileNotEmpty())
                )
            )
        )
    );

    public static final InnerSourceReadinessSpecification PUBLIC_GITHUB_DEFAULT = InnerSourceReadinessSpecification.create(
        "PUBLIC_GITHUB_DEFAULT",
        RepositoryRequirements.create(
            DirectoriesToSearch.create("/", "/docs", "/.github"),
            FileRequirement.create(
                FileToFind.create("/README.md"),
                FileChecks.create(
                    FileCheck.fileNotEmpty(),
                    FileCheck.markdownFileWithTitleHeadingMatching("(?i)^(?!title$).*"),
                    FileCheck.markdownFileWithDescriptionAfterTitle()
                )
            ),
            FileRequirement.create(
                FileToFind.create("/CONTRIBUTING.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/CODE_OF_CONDUCT.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/LICENSE.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/SUPPORT.md"),
                FileChecks.create(FileCheck.fileNotEmpty())
            ),
            FileRequirement.create(
                FileToFind.create("/.github/CODEOWNERS"),
                FileChecks.create(
                    FileCheck.fileNotEmpty(),
                    FileCheck.fileHasLineMatching("^\\s*\\*\\s+(\\S+@\\S+|@\\S+).*$")
                )
            ),
            FileRequirement.create(
                FileToFind.create("/.github/ISSUE_TEMPLATE/"),
                FileChecks.create(
                    FileCheck.directoryNotEmpty(),
                    FileCheck.directoryContainsFileSatisfying(
                        FileChecks.create(
                            FileCheck.fileHasYamlFrontMatterProperties("name", "about")
                        )
                    )
                )
            ),
            FileRequirement.oneOf(
                FileRequirementOption.create(
                    FileToFind.create("/.github/PULL_REQUEST_TEMPLATE/"),
                    FileChecks.create(FileCheck.directoryNotEmpty())
                ),
                FileRequirementOption.create(
                    FileToFind.create("/.github/PULL_REQUEST_TEMPLATE.md"),
                    FileChecks.create(FileCheck.fileNotEmpty())
                )
            )
        )
    );

    InnerSourceReadinessSpecification() {}

    public abstract String specName();

    public abstract RepositoryRequirements repositoryRequirements();

    public String toJson() {
        return JsonService.getInstance().toJson(this);
    }

    public static InnerSourceReadinessSpecification fromJson(final String json)
        throws InvalidInnerSourceReadinessSpecificationException {
        final Set<String> validationErrors;
        try {
            validationErrors =
                JsonService
                    .getInstance()
                    .validateJsonAgainstClasspathSchema(
                        json,
                        "/innerSourceReadinessSpecification.schema.json"
                    );
        } catch (final IOException cause) {
            throw new InvalidInnerSourceReadinessSpecificationException(cause);
        }
        if (!validationErrors.isEmpty()) {
            throw new InvalidInnerSourceReadinessSpecificationException(validationErrors);
        }
        return JsonService
            .getInstance()
            .fromJson(json, InnerSourceReadinessSpecification.class);
    }

    public static InnerSourceReadinessSpecification create(
        final String specName,
        final RepositoryRequirements repositoryRequirements
    ) {
        return ImmutableInnerSourceReadinessSpecification
            .builder()
            .specName(specName)
            .repositoryRequirements(repositoryRequirements)
            .build();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class DirectoriesToSearch {

        DirectoriesToSearch() {}

        public abstract List<String> directoryPaths();

        public static DirectoriesToSearch create(
            final String firstDirectoryPath,
            final String... additionalDirectoryPaths
        ) {
            return ImmutableDirectoriesToSearch
                .builder()
                .directoryPaths(
                    Stream
                        .concat(
                            Stream.of(firstDirectoryPath),
                            Arrays.stream(additionalDirectoryPaths)
                        )
                        .distinct()
                        .collect(Collectors.toList())
                )
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class RepositoryRequirements {

        RepositoryRequirements() {}

        public abstract DirectoriesToSearch directoriesToSearch();

        public abstract List<FileRequirement> requiredFiles();

        public static RepositoryRequirements create(
            final DirectoriesToSearch directoriesToSearch,
            final FileRequirement firstFileRequirements,
            final FileRequirement... remainingFileRequirements
        ) {
            return create(
                directoriesToSearch,
                Stream
                    .concat(
                        Stream.of(firstFileRequirements),
                        Arrays.stream(remainingFileRequirements)
                    )
                    .collect(Collectors.toList())
            );
        }

        public static RepositoryRequirements create(
            final DirectoriesToSearch directoriesToSearch,
            final Iterable<? extends FileRequirement> fileRequirements
        ) {
            return ImmutableRepositoryRequirements
                .builder()
                .directoriesToSearch(directoriesToSearch)
                .requiredFiles(fileRequirements)
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileToFind {

        FileToFind() {}

        public abstract String expectedFilePath();

        public String baseFileName() {
            return FilenameUtils.getBaseName(
                Paths.get(expectedFilePath()).getFileName().toString()
            );
        }

        public static FileToFind create(final String expectedFilePathFromRepoRoot) {
            final Path path = Paths.get(
                StringUtils.prependIfMissing(expectedFilePathFromRepoRoot, "/")
            );
            return ImmutableFileToFind
                .builder()
                .expectedFilePath(path.toAbsolutePath().toString())
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileChecks implements Iterable<FileCheck> {

        FileChecks() {}

        public abstract List<FileCheck> getChecks();

        @Default
        @Override
        public Iterator<FileCheck> iterator() {
            return getChecks().iterator();
        }

        public static FileChecks create(
            final FileCheck firstFileCheck,
            final FileCheck... additionalFileChecks
        ) {
            return ImmutableFileChecks
                .builder()
                .checks(
                    Stream
                        .concat(
                            Stream.of(firstFileCheck),
                            Arrays.stream(additionalFileChecks)
                        )
                        .distinct()
                        .collect(Collectors.toList())
                )
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileRequirementOption {

        FileRequirementOption() {}

        public abstract FileToFind fileToFind();

        public abstract FileChecks getFileChecks();

        public static FileRequirementOption create(
            final FileToFind fileToFind,
            final FileChecks fileChecks
        ) {
            return ImmutableFileRequirementOption
                .builder()
                .fileToFind(fileToFind)
                .fileChecks(fileChecks)
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileRequirement {

        public abstract List<FileRequirementOption> getRequiredFileOptions();

        public static FileRequirement create(
            final FileToFind fileToFind,
            final FileChecks fileChecks
        ) {
            return ImmutableFileRequirement
                .builder()
                .requiredFileOptions(
                    Lists.newArrayList(
                        FileRequirementOption.create(fileToFind, fileChecks)
                    )
                )
                .build();
        }

        public static FileRequirement oneOf(
            final FileRequirementOption firstOption,
            final FileRequirementOption secondOption,
            final FileRequirementOption... additionalOptions
        ) {
            return ImmutableFileRequirement
                .builder()
                .requiredFileOptions(
                    Stream
                        .concat(
                            Stream.of(firstOption, secondOption),
                            Arrays.stream(additionalOptions)
                        )
                        .distinct()
                        .collect(Collectors.toList())
                )
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Gson.ExpectedSubtypes(
        value = {
            FileHasYamlFrontMatterPropertiesCheck.class,
            MarkdownFileHasTitleHeadingCheck.class,
            MarkdownFileHasHeadingCheck.class,
            MarkdownFileHasImageCheck.class,
            DirectoryContainsFileSatisfyingCheck.class,
            FileHasLineMatchingCheck.class,
            FileCheck.class,
        }
    )
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileCheck {

        public abstract String requirement();

        public abstract Map<String, Object> extensionParameters();

        public static FileCheck fileExists() {
            return ImmutableFileCheck.builder().requirement("FILE_EXISTS").build();
        }

        public static FileCheck fileNotEmpty() {
            return ImmutableFileCheck.builder().requirement("FILE_NOT_EMPTY").build();
        }

        public static FileCheck pathMatchesExpected() {
            return ImmutableFileCheck
                .builder()
                .requirement("PATH_MATCHES_EXPECTED")
                .build();
        }

        public static FileCheck fileHasLineMatching(final String regexPattern) {
            return ImmutableFileHasLineMatchingCheck
                .builder()
                .regexPattern(regexPattern)
                .build();
        }

        public static FileCheck directoryExists() {
            return ImmutableFileCheck.builder().requirement("DIRECTORY_EXISTS").build();
        }

        public static FileCheck directoryNotEmpty() {
            return ImmutableFileCheck
                .builder()
                .requirement("DIRECTORY_NOT_EMPTY")
                .build();
        }

        public static FileCheck directoryContainsFileSatisfying(
            final FileChecks fileChecksToSatisfy
        ) {
            return ImmutableDirectoryContainsFileSatisfyingCheck
                .builder()
                .fileChecks(fileChecksToSatisfy)
                .build();
        }

        public static FileCheck fileHasYamlFrontMatterProperties(
            final String... propertyNames
        ) {
            return ImmutableFileHasYamlFrontMatterPropertiesCheck
                .builder()
                .propertyNames(Arrays.asList(propertyNames))
                .build();
        }

        public static FileCheck markdownFileWithTitleHeadingMatching(
            final String titleHeadingRegexPattern
        ) {
            return ImmutableMarkdownFileHasTitleHeadingCheck
                .builder()
                .titleRegexPattern(titleHeadingRegexPattern)
                .build();
        }

        public static FileCheck markdownFileWithDescriptionAfterTitle() {
            return ImmutableFileCheck
                .builder()
                .requirement("MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE")
                .build();
        }

        public static FileCheck markdownFileWithHeading(
            final String heading,
            final Set<String> headingSynonyms,
            final boolean matchCase,
            final boolean matchIfSectionEmpty
        ) {
            return ImmutableMarkdownFileHasHeadingCheck
                .builder()
                .heading(heading)
                .synonyms(headingSynonyms)
                .matchCase(matchCase)
                .matchIfSectionEmpty(matchIfSectionEmpty)
                .build();
        }

        public static FileCheck markdownFileWithImage(
            final String altText,
            final Set<String> altTextSynonyms,
            final boolean matchCase
        ) {
            return ImmutableMarkdownFileHasImageCheck
                .builder()
                .altText(altText)
                .altTextSynonyms(altTextSynonyms)
                .matchCase(matchCase)
                .build();
        }
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileHasLineMatchingCheck extends FileCheck {

        FileHasLineMatchingCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "FILE_HAS_LINE_MATCHING";
        }

        public abstract String regexPattern();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class DirectoryContainsFileSatisfyingCheck extends FileCheck {

        DirectoryContainsFileSatisfyingCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "DIRECTORY_CONTAINS_FILE_SATISFYING";
        }

        public abstract FileChecks fileChecks();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class FileHasYamlFrontMatterPropertiesCheck extends FileCheck {

        FileHasYamlFrontMatterPropertiesCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "FILE_HAS_YAML_FRONT_MATTER_PROPERTIES";
        }

        public abstract Set<String> propertyNames();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class MarkdownFileHasTitleHeadingCheck extends FileCheck {

        MarkdownFileHasTitleHeadingCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "MARKDOWN_FILE_HAS_TITLE_HEADING";
        }

        public abstract String titleRegexPattern();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class MarkdownFileHasHeadingCheck extends FileCheck {

        MarkdownFileHasHeadingCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "MARKDOWN_FILE_HAS_HEADING";
        }

        public abstract String heading();

        public abstract List<String> synonyms();

        public abstract boolean matchCase();

        public abstract boolean matchIfSectionEmpty();
    }

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    @Immutable
    @Style(
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
    )
    public abstract static class MarkdownFileHasImageCheck extends FileCheck {

        MarkdownFileHasImageCheck() {}

        @Derived
        @Override
        public String requirement() {
            return "MARKDOWN_FILE_HAS_IMAGE";
        }

        public abstract String altText();

        public abstract List<String> altTextSynonyms();

        public abstract boolean matchCase();
    }
}
