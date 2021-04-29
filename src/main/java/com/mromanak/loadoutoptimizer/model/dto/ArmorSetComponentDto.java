package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.annotations.MaxTotalSlots;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@MaxTotalSlots(value = 3, message = "Total number of jewel slots must be at most 3")
public class ArmorSetComponentDto {

    @NotBlank(message = "Name must be non-blank")
    private String name;

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
}
