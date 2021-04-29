package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.Loadout;

import java.util.Set;
import java.util.function.Function;

public interface ExLoadoutScoringFunction extends Function<Loadout, Double> {

    Set<String> getDesiredSkills();

    boolean needsDefense();

    boolean needsFireResistance();

    boolean needsWaterResistance();

    boolean needsThunderResistance();

    boolean needsIceResistance();

    boolean needsDragonResistance();

    boolean usesDefenseBuckets();

    int getDefenseBucket(int defense);

    boolean usesResistanceBuckets();

    int getResistanceBucket(int resistance);
}
