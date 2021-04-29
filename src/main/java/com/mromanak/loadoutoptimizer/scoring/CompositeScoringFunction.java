package com.mromanak.loadoutoptimizer.scoring;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeScoringFunction implements LoadoutScoringFunction {

    private final List<LoadoutScoringFunction> scoringFunctions;

    private CompositeScoringFunction(Builder builder) {
        if (builder.scoringFunctions.isEmpty()) {
            throw new IllegalArgumentException("At least one scoringFunction must be provided");
        }
        scoringFunctions = ImmutableList.copyOf(builder.scoringFunctions);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String keyFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");
        return scoringFunctions.stream().
                map(fn -> fn.keyFor(loadout)).
                collect(Collectors.joining("|"));
    }

    @Override
    public double scoreFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");
        return scoringFunctions.stream().
                mapToDouble(fn -> fn.scoreFor(loadout)).
                sum();
    }

    public static final class Builder {
        private final List<LoadoutScoringFunction> scoringFunctions = new ArrayList<>();

        private Builder() {
        }

        public Builder withScoringFunctions(Collection<LoadoutScoringFunction> val) {
            Objects.requireNonNull(val, "scoringFunctions must be non-null");
            scoringFunctions.addAll(val);
            return this;
        }

        public Builder withScoringFunction(LoadoutScoringFunction val) {
            Objects.requireNonNull(val, "scoringFunction must be non-null");
            scoringFunctions.add(val);
            return this;
        }

        public CompositeScoringFunction build() {
            return new CompositeScoringFunction(this);
        }
    }
}
