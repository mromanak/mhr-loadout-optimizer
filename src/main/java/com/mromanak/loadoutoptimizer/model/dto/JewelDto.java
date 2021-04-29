package com.mromanak.loadoutoptimizer.model.dto;

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
public class JewelDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @NotNull(message = "Jewel level must be non-null")
    @Min(value = 1, message = "Jewel level must be at least 1")
    @Max(value = 4, message = "Jewel level must be at most 4")
    private Integer jewelLevel = 1;

    @Valid
    @Size(min = 1, message = "Skills must contain at least one element")
    SortedSet<ProvidedSkillDto> skills;

    @NotNull(message = "Rarity must be non-null")
    @Min(value = 1, message = "Rarity must be at least 1")
    private Integer rarity = 1;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }
}
