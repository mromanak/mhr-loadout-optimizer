package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.scoring.ExLoadoutScoringFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ExOptimizerRequest {

    private final Map<String, Integer> skills;
    private final ArmorType armorType;
    private final double defense;
    private final double fireResistance;
    private final double waterResistance;
    private final double thunderResistance;
    private final double iceResistance;
    private final double dragonResistance;

    private ExOptimizerRequest(Builder builder) {
        skills = ImmutableMap.copyOf(builder.skills);
        armorType = builder.armorType;
        defense = builder.defense;
        fireResistance = builder.fireResistance;
        waterResistance = builder.waterResistance;
        thunderResistance = builder.thunderResistance;
        iceResistance = builder.iceResistance;
        dragonResistance = builder.dragonResistance;
    }

    public static Builder builderForLoadout(Loadout loadout, ArmorType armorType) {
        if(loadout == null) {
            throw new NullPointerException("Loadout must not be null");
        }

        return builder().
            withSkills(loadout.getSkills()).
            withArmorType(armorType).
            withDefense(loadout.getDefense()).
            withFireResistance(loadout.getFireResistance()).
            withWaterResistance(loadout.getWaterResistance()).
            withThunderResistance(loadout.getThunderResistance()).
            withIceResistance(loadout.getIceResistance()).
            withDragonResistance(loadout.getDragonResistance());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExOptimizerRequest copy) {
        Builder builder = new Builder();
        builder.skills = copy.getSkills();
        builder.armorType = copy.getArmorType();
        return builder;
    }

    public static final class Builder {
        private Map<String, Integer> skills = new HashMap<>();
        private ArmorType armorType;
        private double defense = 0;
        private double fireResistance = 0;
        private double waterResistance = 0;
        private double thunderResistance = 0;
        private double iceResistance = 0;
        private double dragonResistance = 0;

        private Builder() {
        }

        public Builder retainSkills(Set<String> skillNames) {
            skills.entrySet().removeIf(e -> e.getKey() != null && !skillNames.contains(e.getKey()));
            return this;
        }

        public Builder retainDefensiveStats(ExLoadoutScoringFunction scoringFunction) {
            if (!scoringFunction.needsDefense()) {
                defense = 0;
            } else if (scoringFunction.usesDefenseBuckets()) {
                defense = scoringFunction.getDefenseBucket((int) defense);
            }
            
            if (!scoringFunction.needsFireResistance()) {
                fireResistance = 0;
            } else if (scoringFunction.usesResistanceBuckets()) {
                fireResistance = scoringFunction.getResistanceBucket((int) fireResistance);
            }

            if (!scoringFunction.needsWaterResistance()) {
                waterResistance = 0;
            } else if (scoringFunction.usesResistanceBuckets()) {
                waterResistance = scoringFunction.getResistanceBucket((int) waterResistance);
            }

            if (!scoringFunction.needsThunderResistance()) {
                thunderResistance = 0;
            } else if (scoringFunction.usesResistanceBuckets()) {
                thunderResistance = scoringFunction.getResistanceBucket((int) thunderResistance);
            }

            if (!scoringFunction.needsIceResistance()) {
                iceResistance = 0;
            } else if (scoringFunction.usesResistanceBuckets()) {
                iceResistance = scoringFunction.getResistanceBucket((int) iceResistance);
            }

            if (!scoringFunction.needsDragonResistance()) {
                dragonResistance = 0;
            } else if (scoringFunction.usesResistanceBuckets()) {
                dragonResistance = scoringFunction.getResistanceBucket((int) dragonResistance);
            }
            
            return this;
        }

        public Builder withSkills(Map<String, Integer> skills) {
            if (skills == null) {
                throw new NullPointerException("Skills map must not be null");
            }
            this.skills = new HashMap<>(skills);
            return this;
        }

        public Builder withArmorType(ArmorType val) {
            armorType = val;
            return this;
        }
        
        public Builder withDefense(double val) {
            defense = val;
            return this;
        }
        
        public Builder withFireResistance(double val) {
            fireResistance = val;
            return this;
        }
        
        public Builder withWaterResistance(double val) {
            waterResistance = val;
            return this;
        }
        
        public Builder withThunderResistance(double val) {
            thunderResistance = val;
            return this;
        }
        
        public Builder withIceResistance(double val) {
            iceResistance = val;
            return this;
        }
        
        public Builder withDragonResistance(double val) {
            dragonResistance = val;
            return this;
        }

        public ExOptimizerRequest build() {
            if (armorType == null) {
                throw new NullPointerException("Armor type must not be null");
            }
            return new ExOptimizerRequest(this);
        }
    }
}
