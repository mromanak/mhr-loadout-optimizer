package com.mromanak.loadoutoptimizer.model.jpa;

import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@ToString(exclude = {"effects", "setBonuses", "armorPieces", "jewels", "uncappingSkill"})
public class Skill {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @NotNull(message = "Max requiredPieces must be non-null")
    @Min(value = 1, message = "Max requiredPieces must be at least 1")
    @Max(value = 7, message = "Max requiredPieces must be at most 7")
    @Column(nullable = false)
    private Integer maxLevel;

    // TODO Validate that this is greater than maxLevel

    @Min(value = 1, message = "Max uncapped requiredPieces must be at least 1")
    @Max(value = 7, message = "Max uncapped requiredPieces must be at most 7")
    private Integer maxUncappedLevel;

    @Valid
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "uncapping_skills",
        joinColumns = @JoinColumn(referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(referencedColumnName = "id")
    )
    private Skill uncappingSkill;

    @NotBlank(message = "Description must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String description;

    // TODO Validate that this has the expected number of entries
    @Valid
    @NotNull(message = "Effects must be non-null")
    @Size(min = 1, max = 7, message = "Effects must contain between 1 and 7, inclusive, entries")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "skill_effect",
        joinColumns = @JoinColumn(referencedColumnName = "id")
    )
    @MapKeyColumn(name = "skill_level")
    @Column(name = "effect", nullable = false, columnDefinition = "varchar")
    private Map<Integer, String> effects;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "skill")
    private List<ArmorPieceSkill> armorPieces = new ArrayList<>();

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "skill")
    private List<JewelSkill> jewels = new ArrayList<>();

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "skill")
    private List<SetBonusSkill> setBonuses = new ArrayList<>();

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }

    public void setArmorPieces(List<ArmorPieceSkill> armorPieces) {
        this.armorPieces.clear();
        if (armorPieces != null) {
            this.armorPieces.addAll(armorPieces);
        }
    }

    public void setJewels(List<JewelSkill> jewels) {
        this.jewels.clear();
        if (jewels != null) {
            this.jewels.addAll(jewels);
        }
    }

    public void setSetBonuses(List<SetBonusSkill> setBonuses) {
        this.setBonuses.clear();
        if (setBonuses != null) {
            this.setBonuses.addAll(setBonuses);
        }
    }
}
