package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.google.auto.service.AutoService;

/**
 * Evaluator for "DIRECTORY_EXISTS" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class DirectoryExistsFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "DIRECTORY_EXISTS";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        return fileToEvaluate.exists() && fileToEvaluate.isDirectory();
    }
}
