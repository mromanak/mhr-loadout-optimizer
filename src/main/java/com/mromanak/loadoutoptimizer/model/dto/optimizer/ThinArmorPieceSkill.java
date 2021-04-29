package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPieceSkill;
import lombok.Data;

@Data
public class ThinArmorPieceSkill {

    private final ThinSkill skill;
    private final Integer skillLevel;

    public ThinArmorPieceSkill(ArmorPieceSkill armorPieceSkill) {
        this.skill = new ThinSkill(armorPieceSkill.getSkill());
        this.skillLevel = armorPieceSkill.getSkillLevel();
    }
}
