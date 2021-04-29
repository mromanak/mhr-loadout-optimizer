package com.mromanak.loadoutoptimizer.model.api;

import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Data
@ApiModel(description = "A request for optimized loadouts")
public class ExLoadoutRequest {

    @ApiModelProperty(
        notes = "A map of the name of a desired skill to an object containing its desired value and relative worth",
        required = true)
    private Map<String, SkillWeight> skillWeights = new HashMap<>();

    @ApiModelProperty(
        notes = "The name of a set bonus skill that the returned loadout must include. Note that this must be the " +
            "name of the skill itself (e.g. Adrenaline or Staminia Cap Up for the Anjanath set), not the name of the " +
            "set bonus itself (e.g. Anajanath Will for the Anjanath set.)")
    private String setBonus;

    @ApiModelProperty(notes = "The relative value of a level 1 decoration slot.")
    private double level1SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 2 decoration slot.")
    private double level2SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 3 decoration slot.")
    private double level3SlotWeight = 0.0;

    @ApiModelProperty(notes = "The relative value of a level 4 decoration slot.")
    private double level4SlotWeight = 0.0;

    @ApiModelProperty(
        notes = "The relative value of including a piece of armor in a loadout. In most cases, this should be a " +
            "small, negative number to encourage the optimizer to use as few pieces of armor as possible.")
    private double loadoutSizeWeight = 0.0;

    private double defenseWeight = 0.0;
    private int defenseBucketSize = 0;
    private double fireResistanceWeight = 0.0;
    private double waterResistanceWeight = 0.0;
    private double thunderResistanceWeight = 0.0;
    private double iceResistanceWeight = 0.0;
    private double dragonResistanceWeight = 0.0;
    private int resistanceBucketSize = 0;
    private double negativeResistanceWeightMultiplier = 1.0;

    @ApiModelProperty(
        notes = "A list of regular expressions that match the names of armor pieces that should not be included in " +
            "the final loadout.")
    private List<Pattern> excludePatterns = new ArrayList<>();

    @ApiModelProperty(
        notes = "The rank of the armor that should be included in the returned loadouts. Since charms do not have an " +
            "easy-to-discover relationship to rank, all charms will be considered for inclusion, no matter how this " +
            "property is set. Defaults to the highest available rank (currently Master Rank).")
    private Rank rank = Rank.MASTER_RANK;

    public void setSkillWeights(Map<String, SkillWeight> skillWeights) {
        this.skillWeights = (skillWeights == null) ? new HashMap<>() : skillWeights;
    }

    public void setExcludePatterns(List<Pattern> excludePatterns) {
        this.excludePatterns = (excludePatterns == null) ? new ArrayList<>() : excludePatterns;
    }

    public void setRank(Rank rank) {
        this.rank = (rank == null) ? Rank.MASTER_RANK : rank;
    }
}

