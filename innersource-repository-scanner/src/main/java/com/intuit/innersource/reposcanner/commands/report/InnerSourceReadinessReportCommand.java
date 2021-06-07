package com.intuit.innersource.reposcanner.commands.report;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import com.intuit.innersource.reposcanner.commands.report.ImmutableFileCheckReport;
import com.intuit.innersource.reposcanner.commands.report.ImmutableFileChecksReport;
import com.intuit.innersource.reposcanner.commands.report.ImmutableFileRequirementReport;
import com.intuit.innersource.reposcanner.commands.report.ImmutableInnerSourceReadinessReport;
import com.intuit.innersource.reposcanner.commands.report.ImmutableInnerSourceReadinessReportCommand;
import com.intuit.innersource.reposcanner.commands.report.InnerSourceReadinessReport.FileCheckReport;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluators;
import com.intuit.innersource.reposcanner.loggingservice.LoggingService;
import com.intuit.innersource.reposcanner.loggingservice.console.ConsoleLoggingService;
import com.intuit.innersource.reposcanner.repofilepath.InvalidRepositoryFilePathException;
import com.intuit.innersource.reposcanner.repofilepath.RepositoryFilePath;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileChecks;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirement;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.RepositoryRequirements;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * A scan command which scans an {@link RepositoryFilePath} representing a checked out GitHub-based git repository root
 * directory. The scanner will scan the repository and check for the desired InnerSource documentation structure as
 * outlined in <a href="https://in/innersource">https://in/innersource</a> - must be connected to Intuit intranet to
 * access.
 *
 * @author Matt Madson
 * @since 1.0.0
 */
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class InnerSourceReadinessReportCommand
    implements Callable<InnerSourceReadinessReport> {

    /**
     * @author Matt Madson
     * @since 1.0.0
     */
    public abstract static class Builder {

        Builder() {}

        public abstract Builder loggingService(LoggingService loggingService);

        public abstract Builder specification(
            InnerSourceReadinessSpecification specification
        );

        public abstract InnerSourceReadinessReportCommand build();
    }

    InnerSourceReadinessReportCommand() {}

    public abstract RepositoryFilePath repoRoot();

    @Default
    public InnerSourceReadinessSpecification specification() {
        return InnerSourceReadinessSpecification.PUBLIC_GITHUB_DEFAULT;
    }

    @Default
    public LoggingService loggingService() {
        return ConsoleLoggingService.INSTANCE;
    }

    /**
     * Static factory method for creating new instances of {@code InnerSourceReadinessReportCommand}.
     *
     * @param repoRoot {@code RepositoryFilePath} pointing to the root directory of a git workspace.
     * @return a new {@link InnerSourceReadinessReportCommand.Builder} which can optionally be configured to override
     * the default behavior of this command.
     * @throws InvalidRepositoryFilePathException if {@code repoRoot} is null.
     */
    public static Builder create(final RepositoryFilePath repoRoot)
        throws InvalidRepositoryFilePathException {
        if (repoRoot == null) {
            throw new InvalidRepositoryFilePathException(
                "repoRoot directory was null, unable to scan for InnerSource readiness"
            );
        }
        return ImmutableInnerSourceReadinessReportCommand.builder().repoRoot(repoRoot);
    }

    @Override
    public InnerSourceReadinessReport call() {
        final LoggingService log = loggingService();
        final InnerSourceReadinessSpecification specification = specification();
        final RepositoryRequirements repositoryRequirements = specification.repositoryRequirements();

        final List<RepositoryFilePath> allFiles = findFilesIn(
            repositoryRequirements.directoriesToSearch().directoryPaths()
        );

        final List<InnerSourceReadinessReport.FileRequirementReport> fileRequirementReports = Lists.newArrayList();

        for (final FileRequirement fileRequirement : repositoryRequirements.requiredFiles()) {
            for (final FileRequirementOption option : fileRequirement.getRequiredFileOptions()) {
                final List<RepositoryFilePath> filesMatchingBaseFileName = allFiles
                    .stream()
                    .filter(
                        file ->
                            StringUtils.equalsIgnoreCase(
                                file.getFileNameWithoutExtension(),
                                option.fileToFind().baseFileName()
                            )
                    )
                    .sorted(
                        Comparator.comparing(
                            RepositoryFilePath::toFilePathString,
                            GitHubFilePathPrecedenceComparator.INSTANCE
                        )
                    )
                    .collect(Collectors.toList());

                final Map<RepositoryFilePath, InnerSourceReadinessReport.FileChecksReport> fileChecksReports = filesMatchingBaseFileName
                    .stream()
                    .collect(
                        Collectors.toMap(
                            Function.identity(),
                            file -> {
                                final FileChecks fileChecks = option.getFileChecks();
                                final MemoizedFileInfo fileInfo = MemoizedFileInfo.of(
                                    file
                                );
                                return ImmutableFileChecksReport
                                    .builder()
                                    .fileEvaluated(file)
                                    .fileChecksEvaluated(fileChecks)
                                    .fileCheckReports(
                                        fileChecks
                                            .getChecks()
                                            .stream()
                                            .map(
                                                req ->
                                                    Maps.immutableEntry(
                                                        req,
                                                        FileCheckEvaluators.getEvaluatorFor(
                                                            req.requirement()
                                                        )
                                                    )
                                            )
                                            .filter(tuple -> tuple.getValue().isPresent())
                                            .map(
                                                tuple ->
                                                    (FileCheckReport) ImmutableFileCheckReport
                                                        .builder()
                                                        .fileCheckEvaluated(
                                                            tuple.getKey()
                                                        )
                                                        .isFileCheckSatisfied(
                                                            tuple
                                                                .getValue()
                                                                .get()
                                                                .evaluate(
                                                                    fileInfo,
                                                                    tuple.getKey(),
                                                                    EvaluationContext.create(
                                                                        specification,
                                                                        fileRequirement,
                                                                        option
                                                                    )
                                                                )
                                                        )
                                                        .build()
                                            )
                                            .collect(Collectors.toList())
                                    )
                                    .build();
                            }
                        )
                    );

                final List<RepositoryFilePath> filesSatisfyingFileChecks = fileChecksReports
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().isFileChecksSatisfied())
                    .map(Map.Entry::getKey)
                    .sorted(
                        Comparator.comparing(
                            RepositoryFilePath::toFilePathString,
                            GitHubFilePathPrecedenceComparator.INSTANCE
                        )
                    )
                    .collect(Collectors.toList());

                fileRequirementReports.add(
                    ImmutableFileRequirementReport
                        .builder()
                        .fileRequirementEvaluated(fileRequirement)
                        .optionEvaluated(option)
                        .filesEvaluated(filesMatchingBaseFileName)
                        .filesSatisfyingFileChecks(filesSatisfyingFileChecks)
                        .fileChecksReports(fileChecksReports.values())
                        .build()
                );
            }
        }

        return ImmutableInnerSourceReadinessReport
            .builder()
            .specificationEvaluated(specification)
            .fileRequirementReports(fileRequirementReports)
            .build();
    }

    @SuppressWarnings("UnstableApiUsage")
    private List<RepositoryFilePath> findFilesIn(final List<String> directoriesToSearch) {
        final RepositoryFilePath repoRoot = repoRoot();

        final Set<String> normalizedDirectoriesToSearch = directoriesToSearch
            .stream()
            .map(
                dirToSearch ->
                    StringUtils.replaceOnce(
                        dirToSearch,
                        "/",
                        StringUtils.appendIfMissing(repoRoot().toFilePathString(), "/")
                    )
            )
            .map(this::lowerCaseNormalizeFilePath)
            .collect(Collectors.toSet());

        return StreamSupport
            .stream(
                Traverser
                    .forTree(
                        (SuccessorsFunction<RepositoryFilePath>) currentDirectory -> {
                            if (!currentDirectory.isDirectory()) {
                                // encountered a file, halting traversal
                                return Lists.newArrayList();
                            }

                            final String normalizedCurrentDirectory = lowerCaseNormalizeFilePath(
                                currentDirectory
                            );

                            if (
                                normalizedDirectoriesToSearch
                                    .stream()
                                    .noneMatch(
                                        targetDirPath ->
                                            targetDirPath.startsWith(
                                                normalizedCurrentDirectory
                                            )
                                    )
                            ) {
                                // traversing directory that is not a target directory or does not lead
                                // to target directory, halt traversal
                                return Lists.newArrayList();
                            }

                            final List<RepositoryFilePath> children = currentDirectory.listAll();
                            if (children.isEmpty()) {
                                // no more children to consider, halting traversal
                                return children;
                            }

                            if (
                                normalizedDirectoriesToSearch.contains(
                                    normalizedCurrentDirectory
                                )
                            ) {
                                // found a target directory, returning all child files
                                return children;
                            }

                            // only return child directories that help us reach the next target directory
                            return children
                                .stream()
                                .filter(RepositoryFilePath::isDirectory)
                                .filter(
                                    childDir ->
                                        normalizedDirectoriesToSearch
                                            .stream()
                                            .anyMatch(
                                                directoryToSearch ->
                                                    directoryToSearch.startsWith(
                                                        lowerCaseNormalizeFilePath(
                                                            childDir
                                                        )
                                                    )
                                            )
                                )
                                .collect(Collectors.toList());
                        }
                    )
                    .breadthFirst(repoRoot)
                    .spliterator(),
                false
            )
            .filter(
                filePath ->
                    normalizedDirectoriesToSearch
                        .stream()
                        .noneMatch(
                            directoryToSearch ->
                                directoryToSearch.equalsIgnoreCase(
                                    lowerCaseNormalizeFilePath(filePath)
                                )
                        )
            )
            .filter(
                filePath ->
                    normalizedDirectoriesToSearch
                        .stream()
                        .anyMatch(
                            directoryToSearch ->
                                lowerCaseNormalizeFilePath(
                                    StringUtils.substringBeforeLast(
                                        filePath.toFilePathString(),
                                        filePath.getFileName()
                                    )
                                )
                                    .equalsIgnoreCase(directoryToSearch)
                        )
            )
            .collect(Collectors.toList());
    }

    private String lowerCaseNormalizeFilePath(final RepositoryFilePath filePath) {
        return lowerCaseNormalizeFilePath(filePath.toFilePathString());
    }

    private String lowerCaseNormalizeFilePath(final String filePath) {
        return StringUtils.removeEnd(filePath.toLowerCase(), "/");
    }
}
