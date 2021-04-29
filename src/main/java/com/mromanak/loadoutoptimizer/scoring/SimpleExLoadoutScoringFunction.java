package com.mromanak.loadoutoptimizer.scoring;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.mromanak.loadoutoptimizer.scoring.SkillScoringUtils.zeroWeightFunction;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleExLoadoutScoringFunction implements ExLoadoutScoringFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleExLoadoutScoringFunction.class);

    private final Map<String, Function<Integer, Double>> skillWieghtFunctions;
    private final double level1SlotWeight;
    private final double level2SlotWeight;
    private final double level3SlotWeight;
    private final double level4SlotWeight;
    private final double defenseWeight;
    private final int defenseBucketSize;
    private final double fireResistanceWeight;
    private final double waterResistanceWeight;
    private final double thunderResistanceWeight;
    private final double iceResistanceWeight;
    private final double dragonResistanceWeight;
    private final int resistanceBucketSize;
    private final double negativeResistanceWeightMultiplier;
    private final Function<Integer, Double> loadoutSizeWeightFunction;

    private SimpleExLoadoutScoringFunction(Builder builder) {
        skillWieghtFunctions = ImmutableMap.copyOf(builder.skillWieghtingFunctions);
        level1SlotWeight = builder.level1SlotWeight;
        level2SlotWeight = builder.level2SlotWeight;
        level3SlotWeight = builder.level3SlotWeight;
        level4SlotWeight = builder.level4SlotWeight;
        defenseWeight = builder.defenseWeight;
        defenseBucketSize = builder.defenseBucketSize;
        fireResistanceWeight = builder.fireResistanceWeight;
        waterResistanceWeight = builder.waterResistanceWeight;
        thunderResistanceWeight = builder.thunderResistanceWeight;
        iceResistanceWeight = builder.iceResistanceWeight;
        dragonResistanceWeight = builder.dragonResistanceWeight;
        resistanceBucketSize = builder.resistanceBucketSize;
        negativeResistanceWeightMultiplier = builder.negativeResistanceWeightMultiplier;
        loadoutSizeWeightFunction = builder.loadoutSizeWeightFunction;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SimpleExLoadoutScoringFunction copy) {
        Builder builder = new Builder();
        builder.skillWieghtingFunctions = new HashMap<>(copy.skillWieghtFunctions);
        builder.level1SlotWeight = copy.level1SlotWeight;
        builder.level2SlotWeight = copy.level2SlotWeight;
        builder.level3SlotWeight = copy.level3SlotWeight;
        builder.level4SlotWeight = copy.level4SlotWeight;
        builder.loadoutSizeWeightFunction = copy.loadoutSizeWeightFunction;
        return builder;
    }

    @Override
    public Set<String> getDesiredSkills() {
        return skillWieghtFunctions.keySet();
    }

    @Override
    public Double apply(Loadout loadout) {
        if(loadout == null) {
            throw new NullPointerException("Loadout must not be null");
        }
        
        double score = 0;
        for(Map.Entry<String, Function<Integer, Double>> entry : skillWieghtFunctions.entrySet()) {
            String skillName = entry.getKey();
            int skillLevel = loadout.getSkills().getOrDefault(skillName, 0);
            Function<Integer, Double> weightFunction = entry.getValue();
            score += weightFunction.apply(skillLevel);
        }
        
        score += level1SlotWeight * loadout.getLevel1Slots();
        score += level2SlotWeight * loadout.getLevel2Slots();
        score += level3SlotWeight * loadout.getLevel3Slots();
        score += level4SlotWeight * loadout.getLevel4Slots();
        score += scoreDefense(loadout.getDefense());
        score += scoreResistance(fireResistanceWeight, loadout.getFireResistance());
        score += scoreResistance(waterResistanceWeight, loadout.getWaterResistance());
        score += scoreResistance(thunderResistanceWeight, loadout.getThunderResistance());
        score += scoreResistance(iceResistanceWeight, loadout.getIceResistance());
        score += scoreResistance(dragonResistanceWeight, loadout.getDragonResistance());
        score += loadoutSizeWeightFunction.apply(loadout.getArmorPieces().size());

        return score;
    }

    private double scoreDefense(int value) {
        if (defenseWeight == 0.0) {
            return 0.0;
        }

        int effectiveValue = value;
        if (defenseBucketSize > 0) {
            effectiveValue = defenseBucketSize * Math.floorDiv(value, defenseBucketSize);
        }

        return defenseWeight * effectiveValue;
    }

    private double scoreResistance(double weight, int value) {
        if (weight == 0.0) {
            return 0.0;
        }

        int effectiveValue = value;
        if (resistanceBucketSize > 0) {
            effectiveValue = resistanceBucketSize * Math.floorDiv(value, resistanceBucketSize);
        }

        if (effectiveValue < 0 && negativeResistanceWeightMultiplier != 1.0) {
            return weight * negativeResistanceWeightMultiplier * effectiveValue;
        } else {
            return weight * effectiveValue;
        }
    }

    @Override
    public boolean needsDefense() {
        return defenseWeight != 0.0;
    }

    @Override
    public boolean needsFireResistance() {
        return fireResistanceWeight != 0.0;
    }

    @Override
    public boolean needsWaterResistance() {
        return waterResistanceWeight != 0.0;
    }

    @Override
    public boolean needsThunderResistance() {
        return thunderResistanceWeight != 0.0;
    }

    @Override
    public boolean needsIceResistance() {
        return iceResistanceWeight != 0.0;
    }

    @Override
    public boolean needsDragonResistance() {
        return dragonResistanceWeight != 0.0;
    }

    @Override
    public boolean usesDefenseBuckets() {
        return defenseBucketSize > 0;
    }

    @Override
    public boolean usesResistanceBuckets() {
        return resistanceBucketSize > 0;
    }

    @Override
    public int getDefenseBucket(int defense) {
        if (!usesDefenseBuckets()) {
            return defense;
        }

        return defenseBucketSize * Math.floorDiv(defense, defenseBucketSize);
    }

    @Override
    public int getResistanceBucket(int resistance) {
        if (!usesResistanceBuckets()) {
            return resistance;
        }

        return resistanceBucketSize * Math.floorDiv(resistance, resistanceBucketSize);
    }

    public static final class Builder {
        private Map<String, Function<Integer, Double>> skillWieghtingFunctions = new HashMap<>();
        private double level1SlotWeight = 0.0;
        private double level2SlotWeight = 0.0;
        private double level3SlotWeight = 0.0;
        private double level4SlotWeight = 0.0;
        private double defenseWeight = 0.0;
        private int defenseBucketSize = 0;
        private double fireResistanceWeight = 0.0;
        private double waterResistanceWeight = 0.0;
        private double thunderResistanceWeight = 0.0;
        private double iceResistanceWeight = 0.0;
        private double dragonResistanceWeight = 0.0;
        private int resistanceBucketSize = 0;
        private double negativeResistanceWeightMultiplier = 1.0;
        private Function<Integer, Double> loadoutSizeWeightFunction = zeroWeightFunction();

        private Builder() {
        }

        public Builder withSkillWieghtingFunctions(Map<String, Function<Integer, Double>> skillWieghtFunctions) {
            if(skillWieghtFunctions == null) {
                throw new NullPointerException("Skill weight functions map must not be null");
            }

            this.skillWieghtingFunctions = skillWieghtFunctions;
            return this;
        }

        public Builder withSkillWeightFunction(String skillName, Function<Integer, Double> weightFunction) {
            if(skillName == null) {
                throw new NullPointerException("Skill name must not be null");
            } else if (weightFunction == null) {
                throw new NullPointerException("Skill weight function must not be null");
            }
            skillWieghtingFunctions.put(skillName, weightFunction);
            return this;
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

        public Builder withDefenseWeight(double val) {
            defenseWeight = val;
            return this;
        }

        public Builder withDefenseBucketSize(int val) {
            defenseBucketSize = Math.max(val, 0);
            return this;
        }

        public Builder withFireResistanceWeight(double val) {
            fireResistanceWeight = val;
            return this;
        }

        public Builder withWaterResistanceWeight(double val) {
            waterResistanceWeight = val;
            return this;
        }

        public Builder withThunderResistanceWeight(double val) {
            thunderResistanceWeight = val;
            return this;
        }

        public Builder withIceResistanceWeight(double val) {
            iceResistanceWeight = val;
            return this;
        }

        public Builder withResistanceBucketSize(int val) {
            resistanceBucketSize = Math.max(val, 0);
            return this;
        }

        public Builder withNegativeResistanceWeightMultiplier(double val) {
            negativeResistanceWeightMultiplier = val;
            return this;
        }

        public Builder withDragonResistanceWeight(double val) {
            dragonResistanceWeight = val;
            return this;
        }

        public Builder withLoadoutSizeWeightFunction(Function<Integer, Double> loadoutSizeWeightFunction) {
            if(loadoutSizeWeightFunction == null) {
                throw new NullPointerException("Loadout size weight function cannot be null");
            }

            this.loadoutSizeWeightFunction = loadoutSizeWeightFunction;
            return this;
        }

        public SimpleExLoadoutScoringFunction build() {
            return new SimpleExLoadoutScoringFunction(this);
        }
    }
}
