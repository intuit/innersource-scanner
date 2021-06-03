package com.github.intuit.innersource.reposcanner.commands.fixup;

import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport;
import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport.FileChecksReport;
import com.github.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReportCommand;
import com.github.intuit.innersource.reposcanner.loggingservice.LoggingService;
import com.github.intuit.innersource.reposcanner.loggingservice.console.ConsoleLoggingService;
import com.github.intuit.innersource.reposcanner.loggingservice.noop.NoopLoggingService;
import com.github.intuit.innersource.reposcanner.repofilepath.InvalidRepositoryFilePathException;
import com.github.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.github.intuit.innersource.reposcanner.repofilepath.github.GitHubRepositoryPath;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirement;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasHeadingCheck;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * A class that represents an immutable command which when executed by calling the {@link #call()} method attempts to
 * <em>fixup</em> the target git repository represented by the {@link RepositoryFilePath} passed to the {@link
 * #create(RepositoryFilePath)} static factory method.
 * <br><br>
 * <p>The fixup algorithm attempts to make the following changes to the specified target repository:
 * <ul>
 *     <li>create any files that are required by the {@link InnerSourceReadinessSpecification}
 * passed to the {@link Builder#specification(InnerSourceReadinessSpecification)} but are either
 * missing or empty in the target repository.</li>
 *      <li>append any markdown
 * headers that are expected to appear in markdown files that are required by the {@link InnerSourceReadinessSpecification}. See: {@link InnerSourceReadinessSpecification.FileCheck#markdownFileWithHeading(String, Set, boolean, boolean)}</li>
 * </ul>
 *
 * <h2><a id="usage">Usage:</a></h2>
 * <pre>
 * List&lt;RepositoryFilePath&gt; filesCreatedOrModified = InnerSourceReadinessFixupCommand.create(
 *         LocalRepositoryFilePath.of(Paths.get("/path/to/local/git/repo))
 *     )
 *     .specification(InnerSourceReadinessSpecification.PUBLIC_GITHUB_DEFAULT)
 *     .fileTemplates(FixupFileTemplates.PUBLIC_GITHUB_DEFAULT)
 *     .build()
 *     .call();
 * </pre>
 *
 * <p>The fixup command also supports fixing up <strong>remote</strong> GitHub repositories by passing a {@link GitHubRepositoryPath}
 * to the {@code create()} static factory.
 * <br><br>
 * <p><strong>NOTE:</strong> The command implements the {@link Callable} interface, so instead of calling
 * directly after building you may also defer execution using the standard {@link java.util.concurrent.ExecutorService}.
 *
 * @author Matt Madson
 * @see FixupFileTemplates
 * @see RepositoryFilePath
 * @see InnerSourceReadinessSpecification
 * @see Callable
 * @see java.util.concurrent.ExecutorService
 * @since 1.0.0
 */
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class InnerSourceReadinessFixupCommand
    implements Callable<List<RepositoryFilePath>> {

    /**
     * A builder for overriding the default properties of the {@link InnerSourceReadinessFixupCommand} and customizing
     * it's execution.
     *
     * @author Matt Madson
     * @since 1.0.0
     */
    public abstract static class Builder {

        Builder() {}

        /**
         * Overrides the default {@link InnerSourceReadinessSpecification} used when executing the {@link
         * InnerSourceReadinessFixupCommand}.
         * <br><br>
         * <p>The <em>default</em> specification is {@link InnerSourceReadinessSpecification#PUBLIC_GITHUB_DEFAULT}.
         * <br><br>
         * <p>The {@code specification} supplied to this builder will determine which
         * files and markdown headings are required. Care should be taken to ensure the {@link
         * #fileTemplates(FixupFileTemplates)} also contains appropriate file templates for any required files expected
         * by this specification.
         *
         * @param specification the {@link InnerSourceReadinessSpecification} to use when executing the {@link
         *                      InnerSourceReadinessFixupCommand}.
         * @return {@code this} builder for chaining
         */
        public abstract Builder specification(
            InnerSourceReadinessSpecification specification
        );

        /**
         * Overrides the default {@link FixupFileTemplates} used when executing the {@link
         * InnerSourceReadinessFixupCommand}.
         * <br><br>
         * <p>The <em>default</em> file templates is {@link FixupFileTemplates#PUBLIC_GITHUB_DEFAULT}.
         * <br><br>
         * <p>The {@code fileTemplates} supplied to this builder will dictate which templates are available
         * to use when creating files that are required by the supplied {@link #specification(InnerSourceReadinessSpecification)}.
         *
         * @param fileTemplates the {@link FixupFileTemplates} to use when executing the {@link
         *                      InnerSourceReadinessFixupCommand}.
         * @return {@code this} builder for chaining
         */
        public abstract Builder fileTemplates(FixupFileTemplates fileTemplates);

        /**
         * Overrides the default {@link LoggingService} used when executing the {@link
         * InnerSourceReadinessFixupCommand}.
         * <br><br>
         * <p>The <em>default</em> LoggingService is {@link ConsoleLoggingService#INSTANCE}.
         * <br><br>
         * <p>The {@code loggingService} supplied to this builder will dictate how and where log messages
         * generated during execution of this command are written.
         *
         * @param loggingService the {@link LoggingService} to use when executing the {@link
         *                       InnerSourceReadinessFixupCommand}.
         * @return {@code this} builder for chaining
         */
        public abstract Builder loggingService(LoggingService loggingService);

        /**
         * Returns an immutable instance of the {@link InnerSourceReadinessFixupCommand} configured with any of the
         * optional properties specified to {@code this} builder.
         *
         * @return the built, immutable {@link InnerSourceReadinessFixupCommand}.
         */
        public abstract InnerSourceReadinessFixupCommand build();
    }

    InnerSourceReadinessFixupCommand() {}

    /**
     * Static factory method for creating new instances of {@code InnerSourceReadinessFixupCommand}. See <a
     * href="usage">Usage</a> for details.
     *
     * @param repoRoot {@code RepositoryFilePath} pointing to the root directory of a git workspace.
     * @return a new {@link Builder} which can optionally be configured to override the default behavior of this
     * command.
     * @throws InvalidRepositoryFilePathException if {@code repoRoot} is null.
     */
    public static Builder create(final RepositoryFilePath repoRoot)
        throws InvalidRepositoryFilePathException {
        if (repoRoot == null) {
            throw new InvalidRepositoryFilePathException(
                "repoRoot directory was null, unable to scan for InnerSource compliance"
            );
        }
        return ImmutableInnerSourceReadinessFixupCommand.builder().repoRoot(repoRoot);
    }

    /**
     * Returns the {@link RepositoryFilePath} to the repository root directory of the repository that will be fixed by
     * execution of {@code this InnerSourceReadinessFixupCommand}.
     *
     * @return the path to the repository root directory of the repository that will be fixed.
     */
    public abstract RepositoryFilePath repoRoot();

    /**
     * Returns the {@link InnerSourceReadinessSpecification} that will be considered when applying fixup logic to the
     * specified target repository.
     * <br><br>
     * <p>The <em>default</em> specification is {@link InnerSourceReadinessSpecification#PUBLIC_GITHUB_DEFAULT}.
     *
     * @return the {@code InnerSourceReadinessSpecification} to use for determining which files and markdown headers are
     * required.
     */
    @Default
    public InnerSourceReadinessSpecification specification() {
        return InnerSourceReadinessSpecification.PUBLIC_GITHUB_DEFAULT;
    }

    /**
     * Returns the {@link FixupFileTemplates} that will be considered when applying fixup logic to the specified target
     * repository.
     * <br><br>
     * <p>The <em>default</em> specification is {@link FixupFileTemplates#PUBLIC_GITHUB_DEFAULT}.
     *
     * @return the {@code FixupFileTemplates} to use for determining which required files have file template contents.
     */
    @Default
    public FixupFileTemplates fileTemplates() {
        return FixupFileTemplates.PUBLIC_GITHUB_DEFAULT;
    }

    /**
     * Returns the {@link LoggingService} that will be used when applying fixup logic to the specified target
     * repository.
     * <br><br>
     * <p>The <em>default</em> specification is {@link ConsoleLoggingService#INSTANCE}.
     *
     * @return the {@code LoggingService} to use when emitting log messages during execution of this fixup command.
     */
    @Default
    public LoggingService loggingService() {
        return ConsoleLoggingService.INSTANCE;
    }

    /**
     * Runs this FixupCommand on the specified repository, with the specified {@link InnerSourceReadinessSpecification}
     * and supplied {@link FixupFileTemplates}.
     *
     * @return a list of {@link RepositoryFilePath} representing files that were either created or modified as a result
     * of executing {@code this} fixup command.
     */
    @Override
    public List<RepositoryFilePath> call() {
        final Map<String, String> emptyFileTemplates = fileTemplates()
            .emptyFileTemplates();
        final RepositoryFilePath repoRoot = repoRoot();
        final LoggingService log = loggingService();
        final InnerSourceReadinessSpecification specification = specification();

        final InnerSourceReadinessReport readinessReport = InnerSourceReadinessReportCommand
            .create(repoRoot)
            .specification(specification)
            .loggingService(NoopLoggingService.INSTANCE)
            .build()
            .call();

        if (readinessReport.isRepositoryInnerSourceReady()) {
            // all readiness requirements already met, nothing to fixup
            return Lists.newArrayList();
        }

        final Set<RepositoryFilePath> fixedFiles = Sets.newHashSet();

        for (final FileRequirement fileRequirement : specification
            .repositoryRequirements()
            .requiredFiles()) {
            if (readinessReport.isFileRequirementSatisfied(fileRequirement)) {
                // requirement already satisfied, nothing to fixup
                continue;
            }

            final Optional<ImmutableTriple<FileRequirementOption, RepositoryFilePath, FileChecksReport>> highestPrecedenceFileScanned = fileRequirement
                .getRequiredFileOptions()
                .stream()
                .map(
                    option ->
                        Pair.of(
                            option,
                            readinessReport.getFileRequirementReportFor(option).get()
                        )
                )
                .flatMap(
                    optionToReport ->
                        optionToReport
                            .getRight()
                            .getFilesEvaluated()
                            .stream()
                            .map(
                                scannedFile ->
                                    ImmutableTriple.of(
                                        optionToReport.getLeft(),
                                        scannedFile,
                                        optionToReport
                                            .getRight()
                                            .getFileChecksReportFor(scannedFile)
                                            .get()
                                    )
                            )
                )
                .limit(1)
                .findFirst();

            final FileRequirementOption optionEvaluated = highestPrecedenceFileScanned
                .map(ImmutableTriple::getLeft)
                .orElse(fileRequirement.getRequiredFileOptions().get(0));

            final RepositoryFilePath fileToFixup = highestPrecedenceFileScanned
                .map(ImmutableTriple::getMiddle)
                .orElse(
                    repoRoot.resolvePath(
                        StringUtils.removeStart(
                            fileRequirement
                                .getRequiredFileOptions()
                                .get(0)
                                .fileToFind()
                                .expectedFilePath(),
                            "/"
                        )
                    )
                );

            final Function<FileCheck, Boolean> isFileCheckSatisfiedFn = highestPrecedenceFileScanned
                .map(ImmutableTriple::getRight)
                .map(
                    fileChecksReport ->
                        (Function<FileCheck, Boolean>) fileChecksReport::isFileCheckSatisfied
                )
                .orElse(fileCheck -> false);

            for (final FileCheck fileCheck : optionEvaluated.getFileChecks()) {
                if (isFileCheckSatisfiedFn.apply(fileCheck)) {
                    // file check satisfied, nothing to fix yet
                    continue;
                }
                switch (fileCheck.requirement()) {
                    case "FILE_NOT_EMPTY":
                        if (
                            !emptyFileTemplates.containsKey(
                                optionEvaluated.fileToFind().expectedFilePath()
                            )
                        ) {
                            log.warn(
                                String.format(
                                    "unable to fixup fileCheck %s for file %s, no empty file template provided",
                                    fileCheck.requirement(),
                                    fileToFixup.toFilePathString()
                                )
                            );
                            continue;
                        }
                        if (!fileToFixup.exists()) {
                            log.info("creating " + fileToFixup.toFilePathString());
                            fileToFixup.touch();
                            fixedFiles.add(fileToFixup);
                        }
                        if (fileToFixup.size() == 0) {
                            fileToFixup.appendLines(
                                Splitter
                                    .on("\n")
                                    .split(
                                        emptyFileTemplates.get(
                                            optionEvaluated
                                                .fileToFind()
                                                .expectedFilePath()
                                        )
                                    )
                            );
                            fixedFiles.add(fileToFixup);
                        }
                        break;
                    case "MARKDOWN_FILE_HAS_HEADING":
                        final MarkdownFileHasHeadingCheck headingRequirement = (MarkdownFileHasHeadingCheck) fileCheck;
                        if (!headingRequirement.matchIfSectionEmpty()) {
                            continue;
                        }
                        if (!fileToFixup.exists()) {
                            log.info("creating " + fileToFixup.toFilePathString());
                            fileToFixup.touch();
                        }
                        fileToFixup.appendLines(
                            Lists.newArrayList(
                                "",
                                "# " + headingRequirement.heading(),
                                ""
                            )
                        );
                        fixedFiles.add(fileToFixup);
                        break;
                    case "FILE_EXISTS":
                        if (!fileToFixup.exists()) {
                            log.info("creating " + fileToFixup.toFilePathString());
                            fileToFixup.touch();
                            fixedFiles.add(fileToFixup);
                        }
                        break;
                    case "DIRECTORY_EXISTS":
                        if (!fileToFixup.exists()) {
                            log.info("creating " + fileToFixup.toFilePathString());
                            fileToFixup.createDirectories();
                            fixedFiles.add(fileToFixup);
                        }
                        break;
                    default:
                        log.warn(
                            String.format(
                                "unable to fixup fileCheck %s for file %s",
                                fileCheck.requirement(),
                                fileToFixup.toFilePathString()
                            )
                        );
                }
            }
        }
        return Lists.newArrayList(fixedFiles);
    }
}
