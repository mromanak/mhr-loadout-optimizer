package com.mromanak.loadoutoptimizer.scoring;

import java.util.function.Function;

public abstract class SkillScoringUtils {

    private static final Function<Integer, Double> ZERO_WEIGHT_FUNCTION = i -> 0.0;

    public static Function<Integer, Double> zeroWeightFunction() {
        return ZERO_WEIGHT_FUNCTION;
    }

    public static Function<Integer, Double> simpleSkillWeightFunction(double weight, int skillMaximum) {
        return skillLevel -> (skillLevel > skillMaximum) ? weight * skillMaximum : weight * skillLevel;
    }
}
