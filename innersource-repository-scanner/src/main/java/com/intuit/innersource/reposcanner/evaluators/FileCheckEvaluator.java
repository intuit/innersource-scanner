package com.intuit.innersource.reposcanner.evaluators;

import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
public interface FileCheckEvaluator {
    String getFileCheckRequirementName();

    boolean evaluate(
        FileInfo fileToEvaluate,
        FileCheck fileCheckToEvaluate,
        EvaluationContext context
    );
}
