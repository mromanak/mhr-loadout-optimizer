package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.mromanak.loadoutoptimizer.model.jpa.SetBonus;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ThinSetBonus {

    private final String id;
    private final String name;
    private final List<ThinArmorPiece> armorPieces;

    public ThinSetBonus(SetBonus setBonus) {
        this.id = setBonus.getId();
        this.name = setBonus.getName();
        this.armorPieces = setBonus.getArmorPieces().stream().
            map(ThinArmorPiece::new).
            collect(toList());
    }
}
