package com.mromanak.loadoutoptimizer.model.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(
    description = "A specification of the desired requiredPieces and relative worth of a skill")
public class SkillWeight {

    @ApiModelProperty(notes = "The maximum number of levels of the skill that the returned loadouts should contain.")
    int maximum = 0;

    @ApiModelProperty(notes = "The relative value of a requiredPieces of the skill.")
    double weight = 0.0;
}
