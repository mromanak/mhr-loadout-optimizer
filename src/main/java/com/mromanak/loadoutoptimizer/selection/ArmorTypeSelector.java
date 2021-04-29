package com.mromanak.loadoutoptimizer.selection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
public class ArmorTypeSelector implements ArmorSelector {

    private final Set<ArmorType> armorTypes;
    private final SelectorMode mode;

    @JsonCreator
    public ArmorTypeSelector(@JsonProperty("armorTypes") Set<ArmorType> armorTypes,
                             @JsonProperty("mode") SelectorMode mode) {
        Objects.requireNonNull(armorTypes, "armorTypes must be non-null");
        Objects.requireNonNull(mode, "mode must be non-null");
        this.armorTypes = ImmutableSet.copyOf(armorTypes);
        this.mode = mode;
    }

    @Override
    public boolean test(ThinArmorPiece armorPiece) {
        Objects.requireNonNull(armorPiece, "armorPiece must be non-null");
        return !armorTypes.contains(armorPiece.getArmorType());
    }
}
