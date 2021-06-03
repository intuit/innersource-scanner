package com.github.intuit.innersource.reposcanner.evaluators.builtin;

import com.github.intuit.innersource.reposcanner.evaluators.EvaluationContext;
import com.github.intuit.innersource.reposcanner.evaluators.FileCheckEvaluator;
import com.github.intuit.innersource.reposcanner.evaluators.FileInfo;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileCheck;
import com.github.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileHasYamlFrontMatterPropertiesCheck;
import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Evaluator for "FILE_HAS_YAML_FRONT_MATTER_PROPERTIES" FileCheck.
 */
@AutoService(FileCheckEvaluator.class)
public final class FileHasYamlFrontMatterPropertiesFileCheckEvaluator
    implements FileCheckEvaluator {

    @Override
    public String getFileCheckRequirementName() {
        return "FILE_HAS_YAML_FRONT_MATTER_PROPERTIES";
    }

    @Override
    public boolean evaluate(
        final FileInfo fileToEvaluate,
        final FileCheck fileCheckToEvaluate,
        final EvaluationContext context
    ) {
        final FileHasYamlFrontMatterPropertiesCheck yamlFrontMatterPropertiesCheck = (FileHasYamlFrontMatterPropertiesCheck) fileCheckToEvaluate;

        final Set<String> frontMatterPropertiesFound = Sets.newHashSet();
        boolean parsingFrontMatter = false;
        for (final String line : fileToEvaluate.getLines()) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if ("---".equals(line)) {
                if (parsingFrontMatter) {
                    break;
                }
                parsingFrontMatter = true;
                continue;
            }
            if (!parsingFrontMatter) {
                break;
            }
            if (!line.contains(":")) {
                continue;
            }
            frontMatterPropertiesFound.add(StringUtils.substringBefore(line, ":"));
        }

        return frontMatterPropertiesFound.containsAll(
            yamlFrontMatterPropertiesCheck.propertyNames()
        );
    }
}
