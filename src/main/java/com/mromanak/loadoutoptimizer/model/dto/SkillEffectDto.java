package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SkillEffectDto implements Comparable<SkillEffectDto> {

    private static final Comparator<SkillEffectDto> COMPARATOR =
        comparing(SkillEffectDto::getLevel, nullsFirst(naturalOrder()));

    @NotNull(message = "Level must be non-null")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 7, message = "Level must be at most 7")
    private Integer level;

    @NotBlank(message = "Description must be non-blank")
    private String description;

    @Override
    public int compareTo(SkillEffectDto that) {
        return COMPARATOR.compare(this, that);
    }
}
