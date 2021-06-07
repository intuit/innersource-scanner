package com.intuit.innersource.reposcanner.evaluators.builtin;

import com.google.auto.service.AutoService;
import com.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.CommentHint;
import com.intuit.innersource.reposcanner.evaluators.MarkdownFileInfo.SectionHeading;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasHeadingCheck;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.MarkdownFileHasTitleHeadingCheck;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluator for "MARKDOWN_FILE_HAS_TITLE_HEADING" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class MarkdownFileHasTitleHeadingFileCheckEvaluator
    implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "MARKDOWN_FILE_HAS_TITLE_HEADING";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final MarkdownFileHasTitleHeadingCheck titleHeadingCheck = (MarkdownFileHasTitleHeadingCheck) fileCheckToEvaluate;
        final MarkdownFileInfo fileInfo = fileToEvaluate.asMarkdownFileInfo();
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
        final boolean firstHeadingIsTitle = fileInfo
            .getHeadings()
            .stream()
            .findFirst()
            .map(SectionHeading::getHeadingText)
            .filter(s -> !reservedHeadingNames.contains(s.toLowerCase()))
            .filter(
                s ->
                    Pattern
                        .compile(titleHeadingCheck.titleRegexPattern())
                        .matcher(s)
                        .matches()
            )
            .isPresent();
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
        return firstHeadingIsTitle || titleCommentHint.isPresent();
    }
}
