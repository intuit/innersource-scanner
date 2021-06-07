package com.intuit.innersource.reposcanner.evaluators.builtin;

import com.google.auto.service.AutoService;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import java.util.Optional;

/**
 * Evaluator for "FILE_NOT_EMPTY" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class FileNotEmptyFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "FILE_NOT_EMPTY";
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
            .filter(f -> !f.isDirectory())
            .map(f -> f.size() > 0)
            .orElse(false);
    }
}
