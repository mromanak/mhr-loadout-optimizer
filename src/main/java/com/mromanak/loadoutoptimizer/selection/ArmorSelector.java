package com.mromanak.loadoutoptimizer.selection;

import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;

import java.util.Objects;
import java.util.function.Predicate;

public interface ArmorSelector extends Predicate<ThinArmorPiece> {

    default boolean test(ArmorPiece armorPiece) {
        Objects.requireNonNull(armorPiece, "armorPiece must be non-null");
        return test(new ThinArmorPiece(armorPiece));
    }
}
