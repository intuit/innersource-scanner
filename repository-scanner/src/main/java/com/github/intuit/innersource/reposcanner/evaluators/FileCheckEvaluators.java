package com.github.intuit.innersource.reposcanner.evaluators;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

/**
 * @author Matt Madson
 * @since 1.0.0
 */
@Immutable(singleton = true, builder = false)
@Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class FileCheckEvaluators {

    public static Optional<FileCheckEvaluator> getEvaluatorFor(
        final String fileCheckRequirementName
    ) {
        return Optional.ofNullable(
            ImmutableFileCheckEvaluators
                .of()
                .getFileCheckEvaluators()
                .get(fileCheckRequirementName)
        );
    }

    @Lazy
    public Map<String, FileCheckEvaluator> getFileCheckEvaluators() {
        try {
            return StreamSupport
                .stream(ServiceLoader.load(FileCheckEvaluator.class).spliterator(), false)
                .map(
                    eval -> Maps.immutableEntry(eval.getFileCheckRequirementName(), eval)
                )
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (l, r) ->
                            FileCheckEvaluators.class.getPackage()
                                    .equals(l.getClass().getPackage())
                                ? l
                                : r,
                        HashMap::new
                    )
                );
        } catch (final Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }
}
