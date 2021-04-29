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
public class ArmorSetSelector implements ArmorSelector {

    private final Set<String> setNames;
    private final SelectorMode mode;

    @JsonCreator
    public ArmorSetSelector(@JsonProperty("setNames") Set<String> setNames,
                            @JsonProperty("mode") SelectorMode mode) {
        Objects.requireNonNull(setNames, "setNames must be non-null");
        Objects.requireNonNull(mode, "mode must be non-null");
        this.setNames = ImmutableSet.copyOf(setNames);
        this.mode = mode;
    }

    @Override
    public boolean test(ThinArmorPiece armorPiece) {
        Objects.requireNonNull(armorPiece, "armorPiece must be non-null");
        switch (mode) {
            case INCLUDE:
                return setNames.contains(armorPiece.getSetName());
            case EXCLUDE:
            default:
                return !setNames.contains(armorPiece.getSetName());
        }
    }
}
