package com.mromanak.loadoutoptimizer.model.dto.optimizer;

import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import lombok.Data;

@Data
public class ThinSkill {

    private final String id;
    private final String name;

    public ThinSkill(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
    }
}
