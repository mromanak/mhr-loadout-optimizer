package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
import com.mromanak.loadoutoptimizer.selection.ArmorSelector;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toSet;

@Service
public class ArmorPieceService {

    private final ArmorPieceRepository armorPieceRepository;

    public ArmorPieceService(ArmorPieceRepository armorPieceRepository) {
        this.armorPieceRepository = armorPieceRepository;
    }

    public Set<ThinArmorPiece> getArmorPiecesWithSkillsAndRank(Set<String> skillNames, Rank rank) {
        return ImmutableSet.copyOf(armorPieceRepository.eagerFindBySkillNameIn(skillNames).
            stream().
            filter((ap) -> ap.getSetType().getRank() == rank).
            map(ThinArmorPiece::new).
            collect(toSet()));
    }

    public Set<ThinArmorPiece> getArmorPiecesWithSkillsAndRank(Set<String> skillNames,
        Rank rank, Predicate<ThinArmorPiece> predicate)
    {
        return ImmutableSet.copyOf(getArmorPiecesWithSkillsAndRank(skillNames, rank).stream().
            filter(predicate).
            collect(toSet()));
    }
}
