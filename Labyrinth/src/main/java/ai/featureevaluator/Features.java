package ai.featureevaluator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Helper functions related to the {@link Feature} interface.
 * 
 * @author Sebastian Oberhoff
 */
public final class Features
{
    private Features()
    {
        // noninstantiable
    }

    public static ImmutableSet<Feature> getAllFeatures()
    {
        Builder<Feature> builder = ImmutableSet.builder();
        builder.add(new DistanceToTreasure());
        builder.add(new IsStandingOnTreasure());
        return builder.build();
    }
}
