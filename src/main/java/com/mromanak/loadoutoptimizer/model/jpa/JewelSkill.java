package com.mromanak.loadoutoptimizer.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class JewelSkill {

    @EmbeddedId
    private PrimaryKey primaryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jewelId")
    private Jewel jewel;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    private Skill skill;

    @NotNull(message = "Skill requiredPieces must be non-null")
    @Min(value = 1, message = "Skill level must be at least 1")
    @Max(value = 7, message = "Skill level must be at most 7")
    @Column(nullable = false)
    private Integer skillLevel;

    public JewelSkill(Jewel jewel, Skill skill, Integer skillLevel) {
        this(new PrimaryKey(jewel.getId(), skill.getId()), jewel, skill, skillLevel);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class PrimaryKey implements Serializable {

        @NotBlank(message = "Jewel ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String jewelId;

        @NotBlank(message = "Skill ID must be non-blank")
        @Column(columnDefinition = "varchar")
        private String skillId;
    }
}
