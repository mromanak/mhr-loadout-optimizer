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
public class SetBonusSkillProviderDto implements Comparable<SetBonusSkillProviderDto> {

    private static final Comparator<SetBonusSkillProviderDto> COMPARATOR =
        comparing(SetBonusSkillProviderDto::getLevel, nullsFirst(naturalOrder())).
        thenComparing(SetBonusSkillProviderDto::getRequiredPieces, nullsFirst(naturalOrder())).
        thenComparing(SetBonusSkillProviderDto::getSourceId, nullsFirst(naturalOrder()));

    @NotBlank(message = "Source ID must be non-blank")
    private String sourceId;

    @NotNull(message = "Required pieces must be non-null")
    @Min(value = 1, message = "Required pieces must be at least 1")
    @Max(value = 5, message = "Required pieces must be at most 5")
    private Integer requiredPieces;

    @NotNull(message = "Required pieces must be non-null")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 7, message = "Level must be at most 7")
    private Integer level;

    @Override
    public int compareTo(SetBonusSkillProviderDto that) {
        return COMPARATOR.compare(this, that);
    }
}
