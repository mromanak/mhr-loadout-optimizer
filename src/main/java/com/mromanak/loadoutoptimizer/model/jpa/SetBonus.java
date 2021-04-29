package com.mromanak.loadoutoptimizer.model.jpa;

import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"armorPieces", "skills"})
public class SetBonus {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "setBonus")
    private List<SetBonusSkill> skills = new ArrayList<>();

    @Valid
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "setBonus")
    private List<ArmorPiece> armorPieces;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }

    public void setSkills(List<SetBonusSkill> skills) {
        this.skills.clear();
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }
}
