package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"setBonus", "skill"})
public class SetBonusSkill {

    @EmbeddedId
    private PrimaryKey primaryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("setBonusId")
    private SetBonus setBonus;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    private Skill skill;

    @NotNull(message = "Required pieces must be non-null")
    @Min(value = 1, message = "Required pieces must be at least 1")
    @Max(value = 5, message = "Required pieces must be at most 5")
    @Column(nullable = false)
    private Integer requiredPieces;

    @NotNull(message = "Required pieces must be non-null")
    @Min(value = 1, message = "Skill level must be at least 1")
    @Max(value = 7, message = "Skill level must be at most 7")
    @Column(nullable = false)
    private Integer skillLevel;

    public SetBonusSkill(SetBonus setBonus, Skill skill, Integer requiredPieces, Integer skillLevel) {
        this(new PrimaryKey(setBonus.getId(), skill.getId()), setBonus, skill, requiredPieces, skillLevel);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class PrimaryKey implements Serializable {

        @NotBlank(message = "Set bonus ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String setBonusId;

        @NotBlank(message = "Skill ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String skillId;
    }
}
