package com.mromanak.loadoutoptimizer.scoring;

import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;

import java.util.Objects;
import java.util.function.Function;

public interface LoadoutScoringFunction {

    String keyFor(Loadout loadout);

    double scoreFor(Loadout loadout);
}
