package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SkillDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @NotNull(message = "Max requiredPieces must be non-null")
    @Min(value = 1, message = "Max requiredPieces must be at least 1")
    @Max(value = 7, message = "Max requiredPieces must be at most 7")
    private Integer maxLevel;

    // TODO Validate that this is greater than maxLevel
    @Min(value = 1, message = "Max uncapped requiredPieces must be at least 1")
    @Max(value = 7, message = "Max uncapped requiredPieces must be at most 7")
    private Integer maxUncappedLevel;

    private String uncappingSkillId;

    @NotBlank(message = "Description must be non-blank")
    private String description;

    // TODO Validate that this has the expected number of entries
    @Valid
    @NotNull(message = "Effects must be non-null")
    @Size(min = 1, max = 7, message = "Effects must contain between 1 and 7, inclusive, entries")
    private SortedSet<SkillEffectDto> effects;

    private SortedSet<SkillProviderDto> armorPieces;

    private SortedSet<SkillProviderDto> jewels;

    private SortedSet<SetBonusSkillProviderDto> setBonuses;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }

    @JsonIgnore
    public Integer getMaxPossibleLevel() {
        if (maxUncappedLevel == null) {
            return maxLevel;
        }
        return maxUncappedLevel;
    }
}
