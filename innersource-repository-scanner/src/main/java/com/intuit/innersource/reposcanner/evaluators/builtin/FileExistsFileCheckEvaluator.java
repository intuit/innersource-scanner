package com.intuit.innersource.reposcanner.evaluators.builtin;

import com.google.auto.service.AutoService;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;

/**
 * Evaluator for "FILE_EXISTS" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class FileExistsFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "FILE_EXISTS";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        return fileToEvaluate.exists() && !fileToEvaluate.isDirectory();
    }
}
