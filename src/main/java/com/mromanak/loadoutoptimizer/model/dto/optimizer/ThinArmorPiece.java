package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ThinArmorPiece {

    private final String id;
    private final String name;
    private final String setName;
    private final ArmorType armorType;
    private final SetType setType;
    private final Integer level1Slots;
    private final Integer level2Slots;
    private final Integer level3Slots;
    private final Integer level4Slots;
    private final List<ThinArmorPieceSkill> skills;
    private final Integer rarity;
    private final Integer defense;
    private final Integer fireResistance;
    private final Integer waterResistance;
    private final Integer thunderResistance;
    private final Integer iceResistance;
    private final Integer dragonResistance;

    public ThinArmorPiece(ArmorPiece armorPiece) {
        this.id = armorPiece.getId();
        this.name = armorPiece.getName();
        this.setName = armorPiece.getSetName();
        this.armorType = armorPiece.getArmorType();
        this.setType = armorPiece.getSetType();
        this.level1Slots = armorPiece.getLevel1Slots();
        this.level2Slots = armorPiece.getLevel2Slots();
        this.level3Slots = armorPiece.getLevel3Slots();
        this.level4Slots = armorPiece.getLevel4Slots();
        this.skills = ImmutableList.copyOf(armorPiece.getSkills().stream().
            map(ThinArmorPieceSkill::new).
            distinct().
            collect(toList()));
        this.rarity = armorPiece.getRarity();
        this.defense = armorPiece.getDefense();
        this.fireResistance = armorPiece.getFireResistance();
        this.waterResistance = armorPiece.getWaterResistance();
        this.thunderResistance = armorPiece.getThunderResistance();
        this.iceResistance = armorPiece.getIceResistance();
        this.dragonResistance = armorPiece.getDragonResistance();
    }
}
