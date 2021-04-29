package com.mromanak.loadoutoptimizer.impl;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import lombok.Data;

@Data
public class OptimizerRequest {
    private final ArmorType armorType;
    private final String key;
}
