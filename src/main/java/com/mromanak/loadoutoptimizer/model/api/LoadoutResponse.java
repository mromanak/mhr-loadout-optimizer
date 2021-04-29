package com.mromanak.loadoutoptimizer.model.api;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(description = "A response containing an optimized loadout")
public class LoadoutResponse {

    @ApiModelProperty(notes = "A map of armor slots to the name of the armor piece recommended for that slot")
    private Map<ArmorType, String> armor;

    @ApiModelProperty(notes = "A map of skill names to skill values granted by the loadout")
    private Map<String, Integer> skills;

    @ApiModelProperty(notes = "The number of unused level 1 decoration slots granted by the loadout")
    private int level1Slots;

    @ApiModelProperty(notes = "The number of unused level 2 decoration slots granted by the loadout")
    private int level2Slots;

    @ApiModelProperty(notes = "The number of unused level 3 decoration slots granted by the loadout")
    private int level3Slots;

    @ApiModelProperty(notes = "The number of unused level 3 decoration slots granted by the loadout")
    private int level4Slots;

    @ApiModelProperty(notes = "The defense level granted by the loadout.")
    private int defense;

    @ApiModelProperty(notes = "The fire resistance level granted by the loadout.")
    private int fireResistance;

    @ApiModelProperty(notes = "The water resistance level granted by the loadout.")
    private int waterResistance;

    @ApiModelProperty(notes = "The thunder resistance level granted by the loadout.")
    private int thunderResistance;

    @ApiModelProperty(notes = "The ice resistance level granted by the loadout.")
    private int iceResistance;

    @ApiModelProperty(notes = "The dragon resistance level granted by the loadout.")
    private int dragonResistance;

    @ApiModelProperty(notes = "Oh god, how do I explain this")
    private String key;

    @ApiModelProperty(notes = "The score of the loadout as determined by the scoring function described in the request")
    private double score;
}
