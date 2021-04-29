package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Joiner;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = DefenseScoringFunction.Builder.class)
public class DefenseScoringFunction implements LoadoutScoringFunction {

    private static final String EMPTY_KEY = "DefenseState=∅";

    private final double defenseWeight;
    private final int defenseBucketSize;
    private final double fireResistanceWeight;
    private final double waterResistanceWeight;
    private final double thunderResistanceWeight;
    private final double iceResistanceWeight;
    private final double dragonResistanceWeight;
    private final int resistanceBucketSize;
    private final double negativeResistanceWeightMultiplier;
    private final ScoringPerformanceMode performanceMode;

    private DefenseScoringFunction(Builder builder) {
        defenseWeight = builder.defenseWeight;
        defenseBucketSize = builder.defenseBucketSize;
        fireResistanceWeight = builder.fireResistanceWeight;
        waterResistanceWeight = builder.waterResistanceWeight;
        thunderResistanceWeight = builder.thunderResistanceWeight;
        iceResistanceWeight = builder.iceResistanceWeight;
        dragonResistanceWeight = builder.dragonResistanceWeight;
        resistanceBucketSize = builder.resistanceBucketSize;
        negativeResistanceWeightMultiplier = builder.negativeResistanceWeightMultiplier;
        performanceMode = builder.performanceMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String keyFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        Map<String, Integer> keyElements = new LinkedHashMap<>();
        if (defenseWeight != 0.0) {
            int defense = bucketFor(loadout.getEffectiveDefense(), defenseBucketSize);
            keyElements.put("Def", defense);
        }
        if (fireResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveFireResistance(), resistanceBucketSize);
            keyElements.put("Fire Res", resistance);
        }
        if (waterResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveWaterResistance(), resistanceBucketSize);
            keyElements.put("Water Res", resistance);
        }
        if (thunderResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveThunderResistance(), resistanceBucketSize);
            keyElements.put("Thunder Res", resistance);
        }
        if (iceResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveIceResistance(), resistanceBucketSize);
            keyElements.put("Ice Res", resistance);
        }
        if (dragonResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveDragonResistance(), resistanceBucketSize);
            keyElements.put("Dragon Res", resistance);
        }

        if (performanceMode == ScoringPerformanceMode.SPEED || keyElements.isEmpty()) {
            return EMPTY_KEY;
        }
        return "DefenseState=" + Joiner.on(';').withKeyValueSeparator(':').join(keyElements);
    }

    @Override
    public double scoreFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        double score = 0.0;
        if (defenseWeight != 0.0) {
            int defense = bucketFor(loadout.getEffectiveDefense(), defenseBucketSize);
            score += scoreDefense(defense);
        }
        if (fireResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveFireResistance(), resistanceBucketSize);
            score += scoreElement(resistance, fireResistanceWeight);
        }
        if (waterResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveWaterResistance(), resistanceBucketSize);
            score += scoreElement(resistance, waterResistanceWeight);
        }
        if (thunderResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveThunderResistance(), resistanceBucketSize);
            score += scoreElement(resistance, thunderResistanceWeight);
        }
        if (iceResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveIceResistance(), resistanceBucketSize);
            score += scoreElement(resistance, iceResistanceWeight);
        }
        if (dragonResistanceWeight != 0.0) {
            int resistance = bucketFor(loadout.getEffectiveDragonResistance(), resistanceBucketSize);
            score += scoreElement(resistance, dragonResistanceWeight);
        }

        return score;
    }

    private int bucketFor(int value, int bucketSize) {
        if (bucketSize > 0) {
            return bucketSize * Math.floorDiv(value, bucketSize);
        }
        return value;
    }

    private double scoreDefense(int defense) {
        return defense * defenseWeight;
    }

    private double scoreElement(int resistance, double weight) {
        if (resistance < 0 && negativeResistanceWeightMultiplier != 1.0) {
            return weight * negativeResistanceWeightMultiplier * resistance;
        } else {
            return weight * resistance;
        }
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private double defenseWeight;
        private int defenseBucketSize;
        private double fireResistanceWeight;
        private double waterResistanceWeight;
        private double thunderResistanceWeight;
        private double iceResistanceWeight;
        private double dragonResistanceWeight;
        private int resistanceBucketSize;
        private double negativeResistanceWeightMultiplier;
        private ScoringPerformanceMode performanceMode = ScoringPerformanceMode.ACCURACY;

        public Builder withDefenseWeight(double defenseWeight) {
            this.defenseWeight = defenseWeight;
            return this;
        }

        public Builder withDefenseBucketSize(int defenseBucketSize) {
            this.defenseBucketSize = defenseBucketSize;
            return this;
        }

        public Builder withFireResistanceWeight(double fireResistanceWeight) {
            this.fireResistanceWeight = fireResistanceWeight;
            return this;
        }

        public Builder withWaterResistanceWeight(double waterResistanceWeight) {
            this.waterResistanceWeight = waterResistanceWeight;
            return this;
        }

        public Builder withThunderResistanceWeight(double thunderResistanceWeight) {
            this.thunderResistanceWeight = thunderResistanceWeight;
            return this;
        }

        public Builder withIceResistanceWeight(double iceResistanceWeight) {
            this.iceResistanceWeight = iceResistanceWeight;
            return this;
        }

        public Builder withDragonResistanceWeight(double dragonResistanceWeight) {
            this.dragonResistanceWeight = dragonResistanceWeight;
            return this;
        }

        public Builder withResistanceBucketSize(int resistanceBucketSize) {
            this.resistanceBucketSize = resistanceBucketSize;
            return this;
        }

        public Builder withNegativeResistanceWeightMultiplier(double negativeResistanceWeightMultiplier) {
            this.negativeResistanceWeightMultiplier = negativeResistanceWeightMultiplier;
            return this;
        }

        public Builder withPerformanceMode(ScoringPerformanceMode val) {
            this.performanceMode = (val == null) ? ScoringPerformanceMode.ACCURACY : val;
            return this;
        }

        public DefenseScoringFunction build() {
            return new DefenseScoringFunction(this);
        }
    }
}
