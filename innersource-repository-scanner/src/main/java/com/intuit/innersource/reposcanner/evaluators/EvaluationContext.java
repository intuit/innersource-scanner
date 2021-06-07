package com.intuit.innersource.reposcanner.evaluators;

import com.intuit.innersource.reposcanner.evaluators.ImmutableEvaluationContext;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirement;
import com.intuit.innersource.reposcanner.specification.InnerSourceReadinessSpecification.FileRequirementOption;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
@Immutable
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class EvaluationContext {

    public static EvaluationContext create(
        final InnerSourceReadinessSpecification readinessSpecification,
        final FileRequirement fileRequirement,
        final FileRequirementOption fileRequirementOption
    ) {
        return ImmutableEvaluationContext
            .builder()
            .readinessSpecification(readinessSpecification)
            .fileRequirement(fileRequirement)
            .optionToEvaluate(fileRequirementOption)
            .build();
    }

    public abstract InnerSourceReadinessSpecification getReadinessSpecification();

    public abstract FileRequirement getFileRequirement();

    public abstract FileRequirementOption getOptionToEvaluate();
}
