package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.google.auto.service.AutoService;
import java.util.Optional;

/**
 * Evaluator for "DIRECTORY_NOT_EMPTY" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class DirectoryNotEmptyFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "DIRECTORY_NOT_EMPTY";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        return Optional
            .ofNullable(fileToEvaluate)
            .filter(FileInfo::exists)
            .filter(FileInfo::isDirectory)
            .map(FileInfo::listAll)
            .map(children -> !children.isEmpty())
            .orElse(false);
    }
}
