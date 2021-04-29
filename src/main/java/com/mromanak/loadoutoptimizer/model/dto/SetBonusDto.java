package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.SortedSet;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SetBonusDto {

    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "Name must be non-blank")
    private String name;

    @Valid
    SortedSet<SetBonusSkillDto> skills;

    @Valid
    SortedSet<String> armorPieces;

    public void setName(String name) {
        this.id = NameUtils.toSlug(name);
        this.name = name;
    }
}
