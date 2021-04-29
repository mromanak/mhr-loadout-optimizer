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
@ToString(exclude = {"armorPiece", "skill"})
public class ArmorPieceSkill {

    @EmbeddedId
    private PrimaryKey primaryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("armorPieceId")
    private ArmorPiece armorPiece;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    private Skill skill;

    @NotNull(message = "Skill requiredPieces must be non-null")
    @Min(value = 1, message = "Skill level must be at least 1")
    @Max(value = 7, message = "Skill level must be at most 7")
    @Column(nullable = false)
    private Integer skillLevel;

    public ArmorPieceSkill(ArmorPiece armorPiece, Skill skill, Integer skillLevel) {
        this(new PrimaryKey(armorPiece.getId(), skill.getId()), armorPiece, skill, skillLevel);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class PrimaryKey implements Serializable {

        @NotBlank(message = "Armor piece ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String armorPieceId;

        @NotBlank(message = "Skill ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String skillId;
    }
}
