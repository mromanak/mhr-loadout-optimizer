package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.Loadout;
import lombok.Data;

@Data
public class LoadoutScoringResult {
    private final String key;
    private final Double score;
    private final Loadout loadout;
}
