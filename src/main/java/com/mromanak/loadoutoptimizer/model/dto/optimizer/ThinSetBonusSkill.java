package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import lombok.Data;

@Data
public class ThinSetBonusSkill {

    private final ThinSetBonus setBonus;
    private final ThinSkill skill;
    private final Integer requiredPieces;

    public ThinSetBonusSkill(SetBonusSkill setBonusSkill) {
        this.setBonus = new ThinSetBonus(setBonusSkill.getSetBonus());
        this.skill = new ThinSkill(setBonusSkill.getSkill());
        this.requiredPieces = setBonusSkill.getRequiredPieces();
    }
}
