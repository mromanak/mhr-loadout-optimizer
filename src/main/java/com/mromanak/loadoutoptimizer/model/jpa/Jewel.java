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

@Data
@Entity
@ToString(exclude = {"skills"})
public class Jewel {

    @Setter(AccessLevel.NONE)
    @Id
    @Column(columnDefinition = "varchar")
    private String id;

    @NotBlank(message = "Name must be non-blank")
    @Column(columnDefinition = "varchar", nullable = false)
    private String name;

    @NotNull(message = "Jewel level must be non-null")
    @Min(value = 1, message = "Jewel level must be at least 1")
    @Max(value = 4, message = "Jewel level must be at most 4")
    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer jewelLevel = 1;

    @Valid
    @Size(min = 1, message = "Skills must contain at least 1 element")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "jewel")
    private List<JewelSkill> skills = new ArrayList<>();

    @NotNull(message = "Rarity must be non-null")
    @Min(value = 1, message = "Rarity must be at least 1")
    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer rarity = 1;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }

    public void setSkills(List<JewelSkill> skills) {
        this.skills.clear();
        if (skills != null) {
            this.skills.addAll(skills);
        }
    }
}
