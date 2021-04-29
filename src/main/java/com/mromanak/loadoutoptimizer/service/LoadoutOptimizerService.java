package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.impl.LoadoutOptimizer;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.api.LoadoutRequest;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import com.mromanak.loadoutoptimizer.scoring.DecorationSlotScoringFunction;
import com.mromanak.loadoutoptimizer.scoring.LoadoutScoringFunction;
import com.mromanak.loadoutoptimizer.scoring.ScoringPerformanceMode;
import com.mromanak.loadoutoptimizer.scoring.SkillScoringFunction;
import com.mromanak.loadoutoptimizer.scoring.SkillScoringFunction.SkillWeight;
import com.mromanak.loadoutoptimizer.selection.ArmorSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class LoadoutOptimizerService {

    private final ArmorPieceService armorPieceService;
    private final SetBonusService setBonusService;

    @Autowired
    public LoadoutOptimizerService(ArmorPieceService armorPieceService, SetBonusService setBonusService) {
        this.armorPieceService = armorPieceService;
        this.setBonusService = setBonusService;
    }

    public LoadoutRequest getSampleRequest() {
        SkillScoringFunction skillScoringFunction = SkillScoringFunction.builder().
                withSkillWeights(ImmutableList.of(
                        new SkillWeight("Earplugs", 5, 1),
                        new SkillWeight("Windproof", 5, 1),
                        new SkillWeight("Tremor Resistance", 3, 1)
                )).
                build();

        DecorationSlotScoringFunction decorationSlotScoringFunction = DecorationSlotScoringFunction.builder().
                withLevel1SlotWeight(0.25).
                withLevel2SlotWeight(0.5).
                withLevel3SlotWeight(0.5).
                withLevel4SlotWeight(0.5).
                withPerformanceMode(ScoringPerformanceMode.SPEED).
                build();

        return LoadoutRequest.builder().
                withRank(Rank.MASTER_RANK).
                withSkillScoringFunction(skillScoringFunction).
                withDecorationSlotScoringFunction(decorationSlotScoringFunction).
                build();
    }

    public List<Loadout> optimize(LoadoutRequest loadoutRequest) {
        ArmorSelector selector = loadoutRequest.getCompositeSelector();
        LoadoutScoringFunction scoringFunction = loadoutRequest.getCompositeScoringFunction();
        Set<String> desiredSkills = loadoutRequest.getSkillScoringFunction().getSkills();

        Rank rank = loadoutRequest.getRank();
        Set<ThinArmorPiece> armorPieces = armorPieceService.getArmorPiecesWithSkillsAndRank(desiredSkills, rank, selector);

        List<Loadout> loadouts;
        if(loadoutRequest.getSetBonuses().isEmpty()) {
            loadouts = LoadoutOptimizer.findBestLoadouts(armorPieces, scoringFunction);
        } else {
            List<Loadout> startingLoadouts = setBonusService.generateStartingLoadoutsFor(loadoutRequest.getSetBonuses(), rank, selector);
            if (startingLoadouts.isEmpty()) {
                String bonusNamesString = String.join(", ", loadoutRequest.getSetBonuses());
                throw new IllegalArgumentException("Could not find a loadout with the given selection criteria that granted all of the following set bonus skills: " + bonusNamesString);
            }

            loadouts = LoadoutOptimizer.findBestLoadoutsGiven(startingLoadouts, armorPieces, scoringFunction);
        }
        return loadouts;
    }
}
