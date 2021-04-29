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
@JsonDeserialize(builder = DecorationSlotScoringFunction.Builder.class)
public class DecorationSlotScoringFunction implements LoadoutScoringFunction {

    private static final String EMPTY_KEY = "DecorationSlotState=∅";

    private final double level1SlotWeight;
    private final double level2SlotWeight;
    private final double level3SlotWeight;
    private final double level4SlotWeight;
    private final ScoringPerformanceMode performanceMode;

    private DecorationSlotScoringFunction(Builder builder) {
        level1SlotWeight = builder.level1SlotWeight;
        level2SlotWeight = builder.level2SlotWeight;
        level3SlotWeight = builder.level3SlotWeight;
        level4SlotWeight = builder.level4SlotWeight;
        performanceMode = builder.performanceMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String keyFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        Map<String, Integer> keyElements = new LinkedHashMap<>();
        if (level1SlotWeight != 0.0) {
            int level1Slots = loadout.getLevel1Slots();
            keyElements.put("Lv1", level1Slots);
        }
        if (level2SlotWeight != 0.0) {
            int level2Slots = loadout.getLevel2Slots();
            keyElements.put("Lv2", level2Slots);
        }
        if (level3SlotWeight != 0.0) {
            int level3Slots = loadout.getLevel3Slots();
            keyElements.put("Lv3", level3Slots);
        }
        if (level4SlotWeight != 0.0) {
            int level4Slots = loadout.getLevel4Slots();
            keyElements.put("Lv4", level4Slots);
        }
        if (performanceMode == ScoringPerformanceMode.SPEED || keyElements.isEmpty()) {
            return EMPTY_KEY;
        }

        return "DecorationSlotState=" + Joiner.on(';').withKeyValueSeparator(':').join(keyElements);
    }

    @Override
    public double scoreFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        double score = 0;
        if (level1SlotWeight != 0.0) {
            int level1Slots = loadout.getLevel1Slots();
            score += level1SlotWeight * level1Slots;
        }
        if (level2SlotWeight != 0.0) {
            int level2Slots = loadout.getLevel2Slots();
            score += level2SlotWeight * level2Slots;
        }
        if (level3SlotWeight != 0.0) {
            int level3Slots = loadout.getLevel3Slots();
            score += level3SlotWeight * level3Slots;
        }
        if (level4SlotWeight != 0.0) {
            int level4Slots = loadout.getLevel4Slots();
            score += level4SlotWeight * level4Slots;
        }

        return score;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private double level1SlotWeight;
        private double level2SlotWeight;
        private double level3SlotWeight;
        private double level4SlotWeight;
        private ScoringPerformanceMode performanceMode = ScoringPerformanceMode.ACCURACY;

        private Builder() {
        }

        public Builder withLevel1SlotWeight(double val) {
            level1SlotWeight = val;
            return this;
        }

        public Builder withLevel2SlotWeight(double val) {
            level2SlotWeight = val;
            return this;
        }

        public Builder withLevel3SlotWeight(double val) {
            level3SlotWeight = val;
            return this;
        }

        public Builder withLevel4SlotWeight(double val) {
            level4SlotWeight = val;
            return this;
        }

        public Builder withPerformanceMode(ScoringPerformanceMode val) {
            this.performanceMode = (val == null) ? ScoringPerformanceMode.ACCURACY : val;
            return this;
        }

        public DecorationSlotScoringFunction build() {
            return new DecorationSlotScoringFunction(this);
        }
    }
}
