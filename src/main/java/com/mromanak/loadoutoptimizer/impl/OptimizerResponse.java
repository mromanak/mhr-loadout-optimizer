package com.mromanak.loadoutoptimizer.impl;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class OptimizerResponse {

    private static final OptimizerResponse EMPTY_RESPONSE = new OptimizerResponse(ImmutableList.of(), 0.0);
    private static final BinaryOperator<OptimizerResponse> MERGER = (OptimizerResponse r1, OptimizerResponse r2) -> {
        if(r1.score > r2.score) {
            return r1;
        } else if (r1.score < r2.score) {
            return r2;
        }

        // Given loadouts with the same score, prefer ones that use the fewest armor pieces
        int minumumLength = concat(r1.armorPiecesToAdd.stream(), r2.armorPiecesToAdd.stream()).
            map(List::size).
            min(Comparator.naturalOrder()).
            orElse(0);
        List<List<ThinArmorPiece>> armorPiecesToAdd = concat(r1.armorPiecesToAdd.stream(), r2.armorPiecesToAdd.stream()).
            filter(l -> l.size() == minumumLength).
            collect(toList());

        return new OptimizerResponse(armorPiecesToAdd, r1.score);
    };

    private final List<List<ThinArmorPiece>> armorPiecesToAdd;
    private final double score;

    public static OptimizerResponse of(List<List<ThinArmorPiece>> armorPiecesToAdd, double score) {
        armorPiecesToAdd = (armorPiecesToAdd == null) ? ImmutableList.of() : ImmutableList.copyOf(armorPiecesToAdd);
        return new OptimizerResponse(armorPiecesToAdd, score);
    }

    public static OptimizerResponse ofLoadouts(List<Loadout> loadouts, double score) {
        if(loadouts == null) {
            return of(ImmutableList.of(), score);
        }
        List<List<ThinArmorPiece>> armorPiecesToAdd = loadouts.stream().
            map(l -> l.getArmorPieces().values()).
            map(ArrayList::new).
            collect(toList());
        return of(armorPiecesToAdd, score);
    }

    public static OptimizerResponse empty() {
        return EMPTY_RESPONSE;
    }

    public static BinaryOperator<OptimizerResponse> merger() {
        return MERGER;
    }
}
