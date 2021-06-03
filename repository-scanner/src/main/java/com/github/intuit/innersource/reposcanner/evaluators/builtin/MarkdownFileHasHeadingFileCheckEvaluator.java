package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.CommentHint;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.SectionHeading;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasHeadingCheck;
import com.google.auto.service.AutoService;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluator for "MARKDOWN_FILE_HAS_HEADING" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class MarkdownFileHasHeadingFileCheckEvaluator
    implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "MARKDOWN_FILE_HAS_HEADING";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final MarkdownFileHasHeadingCheck hasHeadingCheck = (MarkdownFileHasHeadingCheck) fileCheckToEvaluate;
        final MarkdownFileInfo fileInfo = fileToEvaluate.asMarkdownFileInfo();
        return (
            fileInfo
                .getHeadings()
                .stream()
                .filter(h -> hasHeadingCheck.matchIfSectionEmpty() || h.hasContent())
                .map(SectionHeading::getHeadingText)
                .anyMatch(
                    headingText -> {
                        if (hasHeadingCheck.matchCase()) {
                            return (
                                StringUtils.equals(
                                    headingText,
                                    hasHeadingCheck.heading()
                                ) ||
                                hasHeadingCheck.synonyms().contains(headingText)
                            );
                        } else {
                            return (
                                StringUtils.equalsIgnoreCase(
                                    headingText,
                                    hasHeadingCheck.heading()
                                ) ||
                                hasHeadingCheck
                                    .synonyms()
                                    .stream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toSet())
                                    .contains(headingText.toLowerCase())
                            );
                        }
                    }
                ) ||
            fileInfo
                .getCommentHints()
                .stream()
                .map(CommentHint::getHintText)
                .anyMatch(
                    hintText ->
                        StringUtils.equalsIgnoreCase(
                            hintText,
                            hasHeadingCheck.heading()
                        ) ||
                        hasHeadingCheck
                            .synonyms()
                            .stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet())
                            .contains(hintText.toLowerCase())
                )
        );
    }
}
