package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileHasLineMatchingCheck;
import com.google.auto.service.AutoService;
import java.util.regex.Pattern;

/**
 * Evaluator for "FILE_HAS_LINE_MATCHING" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class FileHasLineMatchingFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "FILE_HAS_LINE_MATCHING";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final FileHasLineMatchingCheck lineMatchingCheck = (FileHasLineMatchingCheck) fileCheckToEvaluate;
        final Pattern expectedLinePattern = Pattern.compile(
            lineMatchingCheck.regexPattern()
        );
        return fileToEvaluate
            .getLines()
            .stream()
            .anyMatch(line -> expectedLinePattern.matcher(line).find());
    }
}
