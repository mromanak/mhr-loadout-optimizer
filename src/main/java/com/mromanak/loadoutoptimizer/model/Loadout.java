package com.mromanak.loadoutoptimizer.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPieceSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Data
public class Loadout {

    private static final List<String> ELEMENTAL_RESISTANCE_SKILLS = ImmutableList.of(
            "Fire Resistance",
            "Water Resistance",
            "Thunder Resistance",
            "Ice Resistance",
            "Dragon Resistance"
    );

    private final Map<ArmorType, ThinArmorPiece> armorPieces;
    private final Map<String, Integer> skills;
    private final int level1Slots;
    private final int level2Slots;
    private final int level3Slots;
    private final int level4Slots;
    private final int defense;
    private final int fireResistance;
    private final int waterResistance;
    private final int thunderResistance;
    private final int iceResistance;
    private final int dragonResistance;
    private final double score;

    private Loadout(Builder builder) {
        armorPieces = builder.armorPieces;
        Map<String, Integer> skillsTmp = new TreeMap<>();
        int level1SlotsTmp = 0;
        int level2SlotsTmp = 0;
        int level3SlotsTmp = 0;
        int level4SlotsTmp = 0;
        int defenseTmp = 0;
        int fireResistanceTmp = 0;
        int waterResistanceTmp = 0;
        int thunderResistanceTmp = 0;
        int iceResistanceTmp = 0;
        int dragonResistanceTmp = 0;
        for(ThinArmorPiece armorPiece : armorPieces.values()) {
            for(ThinArmorPieceSkill skillMapping : armorPiece.getSkills()) {
                skillsTmp.merge(skillMapping.getSkill().getName(), skillMapping.getSkillLevel(), Integer::sum);
            }
            level1SlotsTmp += armorPiece.getLevel1Slots();
            level2SlotsTmp += armorPiece.getLevel2Slots();
            level3SlotsTmp += armorPiece.getLevel3Slots();
            level4SlotsTmp += armorPiece.getLevel4Slots();
            defenseTmp += armorPiece.getDefense();
            fireResistanceTmp += armorPiece.getFireResistance();
            waterResistanceTmp += armorPiece.getWaterResistance();
            thunderResistanceTmp += armorPiece.getThunderResistance();
            iceResistanceTmp += armorPiece.getIceResistance();
            dragonResistanceTmp += armorPiece.getDragonResistance();
        }
        skills = ImmutableMap.copyOf(skillsTmp);
        level1Slots = level1SlotsTmp;
        level2Slots = level2SlotsTmp;
        level3Slots = level3SlotsTmp;
        level4Slots = level4SlotsTmp;
        defense = defenseTmp;
        fireResistance = fireResistanceTmp;
        waterResistance = waterResistanceTmp;
        thunderResistance = thunderResistanceTmp;
        iceResistance = iceResistanceTmp;
        dragonResistance = dragonResistanceTmp;
        score = builder.score;
    }

    public static Loadout empty() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Loadout copy) {
        Builder builder = new Builder();
        builder.armorPieces = new TreeMap<>(copy.getArmorPieces());
        return builder;
    }

    public Optional<Loadout> mergeWith(Loadout other) {
        if (other == null) {
            return Optional.of(builder(this).build());
        }

        if(this.armorPieces.size() + other.armorPieces.size() > 6) {
            return Optional.empty();
        }

        for (ArmorType armorType : this.armorPieces.keySet()) {
            if (other.armorPieces.containsKey(armorType)) {
                return Optional.empty();
            }
        }

        Builder builder = builder(this);
        builder.armorPieces.putAll(other.armorPieces);
        return Optional.of(builder.build());
    }

    public int getEffectiveDefense() {
        int defenseTotal = (int) Math.floor((double) this.defense * defenseBonusMultiplier());
        defenseTotal += defenseBonusAdd();
        return defenseTotal;
    }

    private double defenseBonusMultiplier() {
        int defenseBoostLevel = this.skills.getOrDefault("Defense Boost", 0);
        if (defenseBoostLevel < 3) {
            return 1.0;
        }

        switch (defenseBoostLevel) {
            case 3:
            case 4:
                return 1.05;
            case 5:
            case 6:
                return 1.08;
            case 7:
            default:
                return 1.1;
        }
    }

    private int defenseBonusAdd() {
        int defenseBoostLevel = this.skills.getOrDefault("Defense Boost", 0);

        int bonusAdd = 0;
        if (defenseBoostLevel >= 1) {
            switch (defenseBoostLevel) {
                case 1:
                    bonusAdd += 5;
                    break;
                case 2:
                case 3:
                    bonusAdd += 10;
                    break;
                case 4:
                case 5:
                    bonusAdd += 20;
                    break;
                case 6:
                case 7:
                default:
                    bonusAdd += 35;
                    break;
            }
        }

        for (String resistanceSkill : ELEMENTAL_RESISTANCE_SKILLS) {
            int resistanceSkillLevel = this.skills.getOrDefault(resistanceSkill, 0);
            if (resistanceSkillLevel >= 3) {
                bonusAdd += 10;
            }
        }

        return bonusAdd;
    }

    public int getEffectiveFireResistance() {
        return this.fireResistance + resistanceBonusAdd("Fire Resistance");
    }

    public int getEffectiveWaterResistance() {
        return this.waterResistance + resistanceBonusAdd("Water Resistance");
    }

    public int getEffectiveThunderResistance() {
        return this.thunderResistance + resistanceBonusAdd("Thunder Resistance");
    }

    public int getEffectiveIceResistance() {
        return this.iceResistance + resistanceBonusAdd("Ice Resistance");
    }

    public int getEffectiveDragonResistance() {
        return this.dragonResistance + resistanceBonusAdd("Dragon Resistance");
    }

    private int resistanceBonusAdd(String skillName) {
        int bonusAdd = 0;
        int defenseBoostLevel = this.skills.getOrDefault("Defense Boost", 0);
        if (defenseBoostLevel > 3) {
            switch (defenseBoostLevel) {
                case 4:
                case 5:
                    bonusAdd += 3;
                    break;
                case 6:
                case 7:
                default:
                    bonusAdd += 5;
                    break;
            }
        }
        int resistanceSkillLevel = this.skills.getOrDefault(skillName, 0);
        if (resistanceSkillLevel > 0) {
            switch (resistanceSkillLevel) {
                case 1:
                    bonusAdd += 6;
                    break;
                case 2:
                    bonusAdd += 12;
                    break;
                case 3:
                default:
                    bonusAdd += 20;
                    break;
            }
        }

        return bonusAdd;
    }

    public static final class Builder {
        private double score = 0.0;
        private Map<ArmorType, ThinArmorPiece> armorPieces = new TreeMap<>();

        private Builder() {
        }

        public Builder withScore(double score) {
            this.score = score;
            return this;
        }

        public Builder withArmorPiece(ThinArmorPiece val) {
            if(val == null) {
                throw new NullPointerException("Armor piece must not be null");
            }

            armorPieces.put(val.getArmorType(), val);

            return this;
        }

        public Builder withArmorPieces(Iterable<ThinArmorPiece> val) {
            if(val == null) {
                throw new NullPointerException("Armor pieces must not be null");
            }

            for(ThinArmorPiece armorPiece : val) {
                armorPieces.put(armorPiece.getArmorType(), armorPiece);
            }

            return this;
        }

        public Loadout build() {
            return new Loadout(this);
        }
    }
}
