package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize(builder = SkillScoringFunction.Builder.class)
public class SkillScoringFunction implements LoadoutScoringFunction {

    private static final String EMPTY_KEY = "SkillState=∅";

    private final List<SkillWeight> skillWeights;
    private final ScoringPerformanceMode performanceMode;

    private SkillScoringFunction(Builder builder) {
        if (builder.skillWeights.isEmpty()) {
            throw new IllegalArgumentException("skillWeights must be non-empty");
        }

        skillWeights = ImmutableList.copyOf(builder.skillWeights);
        performanceMode = builder.performanceMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String keyFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        SortedMap<String, Integer> keyElements = new TreeMap<>();
        for (SkillWeight skillWeight : skillWeights) {
            String skillName = skillWeight.getName();
            int skillLevel = Math.min(
                    loadout.getSkills().getOrDefault(skillName, 0),
                    skillWeight.getMaximum()
            );
            if (skillLevel > 0) {
                keyElements.put(skillName, skillLevel);
            }
        }
        if (performanceMode == ScoringPerformanceMode.SPEED || keyElements.isEmpty()) {
            return EMPTY_KEY;
        }
        return "SkillState=" + Joiner.on(';').withKeyValueSeparator(':').join(keyElements);
    }

    @Override
    public double scoreFor(Loadout loadout) {
        Objects.requireNonNull(loadout, "loadout must be non-null");

        double score = 0;
        for (SkillWeight skillWeight : skillWeights) {
            String skillName = skillWeight.getName();
            int skillLevel = loadout.getSkills().getOrDefault(skillName, 0);
            score += skillWeight.apply(skillLevel);
        }
        return score;
    }

    @JsonIgnore
    public Set<String> getSkills() {
        return skillWeights.stream().map(SkillWeight::getName).collect(Collectors.toSet());
    }

    @Data
    public static class SkillWeight implements Function<Integer, Double> {

        private final String name;
        private final int maximum;
        private final double weight;

        @JsonCreator
        public SkillWeight(@JsonProperty("name") String name, @JsonProperty("maximum") int maximum,
                           @JsonProperty("weight") double weight) {
            Objects.requireNonNull(name, "name must be non-null");
            Objects.requireNonNull(maximum, "maximum must be non-null");
            Objects.requireNonNull(weight, "weight must be non-null");
            this.name = name;
            this.maximum = maximum;
            this.weight = weight;
        }

        @Override
        public Double apply(Integer skillLevel) {
            return weight * Math.min(skillLevel, maximum);
        }
    }

    public static final class Builder {
        private List<SkillWeight> skillWeights = new ArrayList<>();
        private ScoringPerformanceMode performanceMode = ScoringPerformanceMode.ACCURACY;

        private Builder() {
        }

        public Builder withSkillWeights(List<SkillWeight> val) {
            Objects.requireNonNull(val, "skillWeights must be non-null");
            skillWeights.addAll(val);
            return this;
        }

        public Builder withSkillWeight(SkillWeight val) {
            Objects.requireNonNull(val, "skillWeight must be non-null");
            skillWeights.add(val);
            return this;
        }

        public Builder withPerformanceMode(ScoringPerformanceMode val) {
            performanceMode = (val == null) ? ScoringPerformanceMode.ACCURACY : val;
            return this;
        }

        public SkillScoringFunction build() {
            return new SkillScoringFunction(this);
        }
    }
}
