package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.annotations.MaxTotalSlots;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@MaxTotalSlots(value = 3, message = "Armor piece must have at most 3 jewel slots")
public class ArmorPieceDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @NotBlank(message = "Set name must be non-blank")
    private String setName;

    @NotNull(message = "Armor type must be non-null")
    private ArmorType armorType;

    @NotNull(message = "Set type must be non-null")
    private SetType setType;

    @NotNull(message = "Number of level 1 slots must be non-null")
    @Min(value = 0, message = "Number of level 1 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 1 jewel slots must be at most 3")
    private Integer level1Slots = 0;

    @NotNull(message = "Number of level 2 slots must be non-null")
    @Min(value = 0, message = "Number of level 2 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 2 jewel slots must be at most 3")
    private Integer level2Slots = 0;

    @NotNull(message = "Number of level 3 slots must be non-null")
    @Min(value = 0, message = "Number of level 3 jewel slots must be at least 0")
    @Max(value = 3, message = "Number of level 3 jewel slots must be at most 3")
    private Integer level3Slots = 0;

    @NotNull(message = "Number of level 4 slots must be non-null")
    @Min(value = 0, message = "Number of level 4 slots must be at least 0")
    @Max(value = 3, message = "Number of level 4 slots must be at most 3")
    private Integer level4Slots = 0;

    @Valid
    private SortedSet<ProvidedSkillDto> skills;

    private String setBonusId;

    @NotNull(message = "Rarity must be non-null")
    @Min(value = 1, message = "Rarity must be at least 1")
    private Integer rarity = 1;

    @NotNull(message = "Defense must be non-null")
    @Min(value = 0, message = "Defense must be non-negative")
    private Integer defense = 0;

    @NotNull(message = "Fire resistance must be non-null")
    private Integer fireResistance = 0;

    @NotNull(message = "Water resistance must be non-null")
    private Integer waterResistance = 0;

    @NotNull(message = "Thunder resistance must be non-null")
    private Integer thunderResistance = 0;

    @NotNull(message = "Ice resistance must be non-null")
    private Integer iceResistance = 0;

    @NotNull(message = "Dragon resistance must be non-null")
    private Integer dragonResistance = 0;

    // TODO Find a less-redundant, similarly-compact solution for deriving id from these properties
    public void setSetName(String setName) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.setName = setName;
    }

    public void setArmorType(ArmorType armorType) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.armorType = armorType;
    }

    public void setSetType(SetType setType) {
        this.id = NameUtils.toSlug(setName, armorType, setType);
        this.setType = setType;
    }
}
