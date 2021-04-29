package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.Data;

import java.util.Objects;

@Data
@JsonDeserialize(builder = SizeScoringFunction.Builder.class)
public class SizeScoringFunction implements LoadoutScoringFunction {

    private static final String EMPTY_KEY = "SizeState=∅";

    private final double sizeWeight;
    private final ScoringPerformanceMode performanceMode;

    private SizeScoringFunction(Builder builder) {
        sizeWeight = builder.sizeWeight;
        performanceMode = builder.performanceMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String keyFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        if (performanceMode == ScoringPerformanceMode.SPEED || loadout.getArmorPieces().isEmpty()) {
            return EMPTY_KEY;
        }
        return "SizeState=" + loadout.getArmorPieces().size();
    }

    @Override
    public double scoreFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        return sizeWeight * loadout.getArmorPieces().size();
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private double sizeWeight = 0;
        private ScoringPerformanceMode performanceMode = ScoringPerformanceMode.ACCURACY;

        private Builder() {
        }

        public Builder withSizeWeight(double val) {
            sizeWeight = val;
            return this;
        }

        public Builder withPerformanceMode(ScoringPerformanceMode val) {
            performanceMode = val;
            return this;
        }

        public SizeScoringFunction build() {
            return new SizeScoringFunction(this);
        }
    }
}
