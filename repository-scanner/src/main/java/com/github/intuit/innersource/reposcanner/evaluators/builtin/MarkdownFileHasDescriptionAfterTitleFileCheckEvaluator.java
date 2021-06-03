package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.CommentHint;
import com.github.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.SectionHeading;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasHeadingCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasTitleHeadingCheck;
import com.google.auto.service.AutoService;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluator for "MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class MarkdownFileHasDescriptionAfterTitleFileCheckEvaluator
    implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "MARKDOWN_FILE_HAS_DESCRIPTION_AFTER_TITLE";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final MarkdownFileInfo fileInfo = fileToEvaluate.asMarkdownFileInfo();
        final String titleRegexPattern = context
            .getOptionToEvaluate()
            .getFileChecks()
            .getChecks()
            .stream()
            .filter(
                req ->
                    "MARKDOWN_FILE_HAS_TITLE_HEADING".equalsIgnoreCase(req.requirement())
            )
            .map(req -> (MarkdownFileHasTitleHeadingCheck) req)
            .findFirst()
            .map(MarkdownFileHasTitleHeadingCheck::titleRegexPattern)
            .orElse(".+");
        final Set<String> reservedHeadingNames = context
            .getOptionToEvaluate()
            .getFileChecks()
            .getChecks()
            .stream()
            .filter(
                req -> "MARKDOWN_FILE_HAS_HEADING".equalsIgnoreCase(req.requirement())
            )
            .map(req -> (MarkdownFileHasHeadingCheck) req)
            .flatMap(
                req -> Stream.concat(Stream.of(req.heading()), req.synonyms().stream())
            )
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
        final boolean titleHeadingHasDescription = fileInfo
            .getHeadings()
            .stream()
            .findFirst()
            .filter(s -> !reservedHeadingNames.contains(s.getHeadingText().toLowerCase()))
            .filter(
                s ->
                    Pattern
                        .compile(titleRegexPattern)
                        .matcher(s.getHeadingText())
                        .matches()
            )
            .map(SectionHeading::hasContent)
            .orElse(false);
        final Optional<CommentHint> titleCommentHint = fileInfo
            .getCommentHints()
            .stream()
            .filter(
                hint ->
                    StringUtils.equalsIgnoreCase(
                        StringUtils.trimToNull(hint.getHintText()),
                        "title"
                    )
            )
            .findFirst();
        final boolean titleCommentHintHasDescription = titleCommentHint
            .map(CommentHint::hintedElementHasDescription)
            .orElse(false);
        final Optional<CommentHint> descriptionCommentHint = fileInfo
            .getCommentHints()
            .stream()
            .filter(
                hint ->
                    StringUtils.equalsIgnoreCase(
                        StringUtils.trimToNull(hint.getHintText()),
                        "description"
                    )
            )
            .findFirst();
        return (
            (titleHeadingHasDescription && !titleCommentHint.isPresent()) ||
            titleCommentHintHasDescription ||
            descriptionCommentHint.isPresent()
        );
    }
}
