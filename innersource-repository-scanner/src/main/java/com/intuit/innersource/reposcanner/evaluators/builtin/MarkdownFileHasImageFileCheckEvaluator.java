package com.intuit.innersource.reposcanner.evaluators.builtin;

import com.google.auto.service.AutoService;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.CommentHint;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.ImageAltText;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasImageCheck;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluator for "MARKDOWN_FILE_HAS_IMAGE" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class MarkdownFileHasImageFileCheckEvaluator implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "MARKDOWN_FILE_HAS_IMAGE";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final MarkdownFileHasImageCheck imageCheck = (MarkdownFileHasImageCheck) fileCheckToEvaluate;
        final MarkdownFileInfo fileInfo = fileToEvaluate.asMarkdownFileInfo();
        return (
            fileInfo
                .getImageAltTexts()
                .stream()
                .map(ImageAltText::getImageAltText)
                .anyMatch(
                    imageAltText -> {
                        if (imageCheck.matchCase()) {
                            return (
                                StringUtils.equals(imageAltText, imageCheck.altText()) ||
                                imageCheck.altTextSynonyms().contains(imageAltText)
                            );
                        } else {
                            return (
                                StringUtils.equalsIgnoreCase(
                                    imageAltText,
                                    imageCheck.altText()
                                ) ||
                                imageCheck
                                    .altTextSynonyms()
                                    .stream()
                                    .map(String::toLowerCase)
                                    .collect(Collectors.toSet())
                                    .contains(imageAltText.toLowerCase())
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
                        StringUtils.equalsIgnoreCase(hintText, imageCheck.altText()) ||
                        imageCheck
                            .altTextSynonyms()
                            .stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet())
                            .contains(hintText.toLowerCase())
                )
        );
    }
}
