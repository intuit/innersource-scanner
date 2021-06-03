package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluators;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.DirectoryContainsFileSatisfyingCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileChecks;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import com.google.auto.service.AutoService;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Optional;

/**
 * Evaluator for "DIRECTORY_CONTAINS_FILE_SATISFYING" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class DirectoryContainsFileSatisfyingFileCheckEvaluator
    implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "DIRECTORY_CONTAINS_FILE_SATISFYING";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final DirectoryContainsFileSatisfyingCheck directoryContainsFileSatisfyingCheck = (DirectoryContainsFileSatisfyingCheck) fileCheckToEvaluate;
        final FileChecks fileChecksToSatisfy = directoryContainsFileSatisfyingCheck.fileChecks();
        return Optional
            .of(fileToEvaluate)
            .filter(FileInfo::isDirectory)
            .map(FileInfo::listAll)
            .map(List::stream)
            .map(
                files ->
                    files.anyMatch(
                        file ->
                            fileChecksToSatisfy
                                .getChecks()
                                .stream()
                                .map(
                                    check ->
                                        Maps.immutableEntry(
                                            check,
                                            FileCheckEvaluators
                                                .getEvaluatorFor(check.requirement())
                                                .get()
                                        )
                                )
                                .allMatch(
                                    checkToEvaluator ->
                                        checkToEvaluator
                                            .getValue()
                                            .evaluate(
                                                file,
                                                checkToEvaluator.getKey(),
                                                EvaluationContext.create(
                                                    context.getReadinessSpecification(),
                                                    context.getFileRequirement(),
                                                    FileRequirementOption.create(
                                                        context
                                                            .getOptionToEvaluate()
                                                            .fileToFind(),
                                                        fileChecksToSatisfy
                                                    )
                                                )
                                            )
                                )
                    )
            )
            .orElse(false);
    }
}
