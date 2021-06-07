package com.intuit.innersource.reposcanner.evaluators.builtin;

import com.google.auto.service.AutoService;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
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
