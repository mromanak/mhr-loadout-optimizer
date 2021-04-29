package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.scoring.LoadoutScoringFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.hasNextArmorType;
import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.nextArmorType;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoadoutOptimizer {

    private final Map<ArmorType, List<ThinArmorPiece>> armorPieces;
    private final LoadoutScoringFunction scoringFunction;
    private final Map<OptimizerRequest, OptimizerResponse> solutionCache = new HashMap<>();

    public static List<Loadout> findBestLoadouts(Collection<ThinArmorPiece> armorPieces,
        LoadoutScoringFunction scoringFunction)
    {
        return findBestLoadoutsGiven(Loadout.empty(), armorPieces, scoringFunction);
    }

    private static List<Loadout> findBestLoadoutsGiven(Loadout startingLoadout, Collection<ThinArmorPiece> armorPieces,
        LoadoutScoringFunction scoringFunction)
    {
        if(armorPieces == null || armorPieces.isEmpty()) {
            return ImmutableList.of();
        }

        Map<ArmorType, List<ThinArmorPiece>> armorPiecesMap = armorPieces.stream().
            collect(toMap(
                ThinArmorPiece::getArmorType,
                ImmutableList::of,
                (l1, l2) -> ImmutableList.<ThinArmorPiece>builder().addAll(l1).addAll(l2).build()
            ));
        LoadoutOptimizer optimizer = new LoadoutOptimizer(armorPiecesMap, scoringFunction);
        OptimizerResponse response = optimizer.findBestLoadoutsGiven(startingLoadout, nextArmorType(null));
        return response.getArmorPiecesToAdd().
            stream().
            map((List<ThinArmorPiece> newArmorPieces) -> {
                Loadout tmp = Loadout.builder(startingLoadout).withArmorPieces(newArmorPieces).build();
                return Loadout.builder(tmp).withScore(scoringFunction.scoreFor(tmp)).build();
            }).
            collect(toList());
    }

    public static List<Loadout> findBestLoadoutsGiven(List<Loadout> startingLoadouts,
        Collection<ThinArmorPiece> otherArmorPieces, LoadoutScoringFunction scoringFunction)
    {
        return startingLoadouts.stream().
            map(startingLoadout -> findBestLoadoutsGiven(startingLoadout, otherArmorPieces, scoringFunction)).
            map((List<Loadout> loadouts) -> {
                if(loadouts.isEmpty()) {
                    return OptimizerResponse.empty();
                }

                return OptimizerResponse.ofLoadouts(loadouts, scoringFunction.scoreFor(loadouts.get(0)));
            }).
            reduce(OptimizerResponse.empty(), OptimizerResponse.merger()).
            getArmorPiecesToAdd().
            stream().
            map((List<ThinArmorPiece> armorPieces) -> {
                Loadout tmp = Loadout.builder().withArmorPieces(armorPieces).build();
                return Loadout.builder(tmp).withScore(scoringFunction.scoreFor(tmp)).build();
            }).
            distinct().
            collect(toList());
    }

    private OptimizerResponse findBestLoadoutsGiven(Loadout currentLoadout, ArmorType armorType) {
        if(currentLoadout.getArmorPieces().containsKey(armorType)) {
            if(hasNextArmorType(armorType)) {
                return findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
            } else {
                return OptimizerResponse.of(emptyList(), scoringFunction.scoreFor(currentLoadout));
            }
        }

        OptimizerRequest request = new OptimizerRequest(
                armorType,
                scoringFunction.keyFor(currentLoadout)
        );
        if(solutionCache.containsKey(request)) {
            return solutionCache.get(request);
        } else if(hasNextArmorType(armorType)) {
            List<ThinArmorPiece> currentArmorPieces = armorPieces.getOrDefault(armorType, emptyList());
            if(currentArmorPieces.isEmpty()) {
                OptimizerResponse response = findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
                solutionCache.put(request, response);
                return response;
            }

            OptimizerResponse responseWithType = currentArmorPieces.stream().
                map(armorPiece -> optimizeNonTerminal(currentLoadout, armorType, armorPiece)).
                reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
            OptimizerResponse responseWithoutType = findBestLoadoutsGiven(currentLoadout, nextArmorType(armorType));
            OptimizerResponse response = OptimizerResponse.merger().apply(responseWithType, responseWithoutType);
            solutionCache.put(request, response);
            return response;
        } else {
            OptimizerResponse responseWithType = armorPieces.getOrDefault(armorType, emptyList()).
                stream().
                map(armorPiece-> optimizeTerminal(currentLoadout, armorPiece)).
                reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
            OptimizerResponse responseWithoutType = OptimizerResponse.of(emptyList(), scoringFunction.scoreFor(currentLoadout));
            OptimizerResponse response = OptimizerResponse.merger().apply(responseWithType, responseWithoutType);
            solutionCache.put(request, response);
            return response;
        }
    }

    private OptimizerResponse optimizeNonTerminal(Loadout currentLoadout, ArmorType armorType, ThinArmorPiece armorPiece) {
        Loadout nextLoadout = Loadout.builder(currentLoadout).withArmorPiece(armorPiece).build();
        OptimizerResponse nextResponse = findBestLoadoutsGiven(nextLoadout, nextArmorType(armorType));

        if(nextResponse.getArmorPiecesToAdd().isEmpty()) {
            double score = scoringFunction.scoreFor(nextLoadout);
            return OptimizerResponse.of(ImmutableList.of(ImmutableList.of(armorPiece)), score);
        }

        return findBestLoadoutsGiven(nextLoadout, nextArmorType(armorType)).
            getArmorPiecesToAdd().
            stream().
            map((List<ThinArmorPiece> ps) -> {
                List<ThinArmorPiece> nextPiecesToAdd = ImmutableList.<ThinArmorPiece> builder().add(armorPiece).addAll(ps).build();
                Loadout loadout = Loadout.builder(nextLoadout).withArmorPieces(nextPiecesToAdd).build();
                double score = scoringFunction.scoreFor(loadout);
                return OptimizerResponse.of(ImmutableList.of(nextPiecesToAdd), score);
            }).
            reduce(OptimizerResponse.empty(), OptimizerResponse.merger());
    }

    private OptimizerResponse optimizeTerminal(Loadout currentLoadout, ThinArmorPiece armorPiece) {
        List<ThinArmorPiece> armorPiecesToAdd = ImmutableList.of(armorPiece);
        Loadout resultingLoadout = Loadout.builder(currentLoadout).withArmorPiece(armorPiece).build();
        double score = scoringFunction.scoreFor(resultingLoadout);
        return OptimizerResponse.of(ImmutableList.of(armorPiecesToAdd), score);
    }
}
