package com.mromanak.loadoutoptimizer.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinSetBonusSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import com.mromanak.loadoutoptimizer.repository.SetBonusRepository;
import com.mromanak.loadoutoptimizer.selection.ArmorSelector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.hasNextArmorType;
import static com.mromanak.loadoutoptimizer.model.jpa.ArmorType.nextArmorType;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

@Service
public class SetBonusService {

    private final SetBonusRepository setBonusRepository;

    public SetBonusService(SetBonusRepository setBonusRepository) {
        this.setBonusRepository = setBonusRepository;
    }

    public Set<ThinSetBonusSkill> getSetBonusSkillsForRank(String skillName, Rank rank) {

        return ImmutableSet.copyOf(setBonusRepository.eagerFindBySkillName(skillName).stream().
            map((SetBonusSkill sbsk) -> {
                sbsk.getSetBonus().getArmorPieces().
                    removeIf((ap) -> ap.getSetType().getRank() != rank);
                return new ThinSetBonusSkill(sbsk);
            }).
            collect(toSet())
        );
    }

    public List<Loadout> generateStartingLoadoutsFor(List<String> bonusNames, Rank rank, ArmorSelector armorSelector) {
        Objects.requireNonNull(bonusNames, "bonusNames must be non-null");
        Objects.requireNonNull(rank, "rank must be non-null");

        // TODO Detect when one set bonus can provide more than one requested set bonus skill
        List<Loadout> mergedLoadouts;
        if (bonusNames.isEmpty()) {
            throw new IllegalArgumentException("bonusNames must be non-empty");
        } else if (bonusNames.size() == 1) {
            mergedLoadouts = generateStartingLoadoutsFor(bonusNames.get(0), rank, armorSelector);
        } else if (bonusNames.size() > 5) {
            throw new IllegalArgumentException("bonusNames must have at most 5 elements");
        } else {
            List<List<Loadout>> unmergedLoadouts = bonusNames.stream().
                    map(bonusName -> generateStartingLoadoutsFor(bonusName, rank, armorSelector)).
                    collect(toList());

            Optional<List<Loadout>> mergedLoadoutsOpt = unmergedLoadouts.stream().
                    reduce((List<Loadout> loadouts1, List<Loadout> loadouts2) -> {
                        if (loadouts1.isEmpty() || loadouts2.isEmpty()) {
                            return new ArrayList<>();
                        }

                        List<Loadout> mergedLoadoutsTmp = new ArrayList<>(loadouts1.size() * loadouts2.size());
                        for (Loadout loadout1 : loadouts1) {
                            for (Loadout loadout2 : loadouts2) {
                                Optional<Loadout> mergedLoadout = loadout1.mergeWith(loadout2);
                                mergedLoadout.ifPresent(mergedLoadoutsTmp::add);
                            }
                        }
                        return mergedLoadoutsTmp;
                    });

            mergedLoadouts = mergedLoadoutsOpt.orElse(new ArrayList<>());
        }

        // Account for the fact that Inheritance has the combined effect of all Secret set bonus skills
        if (bonusNames.stream().anyMatch(bn -> StringUtils.endsWith(bn, " Secret"))) {
            List<String> nonSecretBonusNames = bonusNames.stream().
                    filter(bn -> !StringUtils.endsWith(bn, " Secret")).
                    collect(toList());
            nonSecretBonusNames.add("Inheritance");
            mergedLoadouts.addAll(generateStartingLoadoutsFor(nonSecretBonusNames, rank, armorSelector));
        }

        return mergedLoadouts;
    }

    private List<Loadout> generateStartingLoadoutsFor(String bonusName, Rank rank, ArmorSelector armorSelector) {
        Set<ThinSetBonusSkill> setBonusSkills = getSetBonusSkillsForRank(bonusName, rank);
        if(setBonusSkills.isEmpty()) {
            throw new IllegalArgumentException("Could not find a set bonus that provides the set bonus skill " + bonusName);
        }

        List<Loadout> loadouts = new ArrayList<>();
        for(ThinSetBonusSkill setBonusSkill : setBonusSkills) {
            Set<ThinArmorPiece> armorPieces = setBonusSkill.getSetBonus().getArmorPieces().stream().
                    filter(armorSelector).
                    collect(toSet());
            int minimumPieces = setBonusSkill.getRequiredPieces();
            long distinctArmorTypes = armorPieces.stream().
                    map(ThinArmorPiece::getArmorType).
                    distinct().
                    count();

            if (distinctArmorTypes >= minimumPieces) {
                loadouts.addAll(generateCombinations(armorPieces, minimumPieces));
            }
        }
        return loadouts;
    }

    private List<Loadout> generateCombinations(Set<ThinArmorPiece> armorPieces, int minimumPieces) {
        Map<ArmorType, List<ThinArmorPiece>> armorPiecesMap = armorPieces.stream().
                collect(toMap(
                        ThinArmorPiece::getArmorType,
                        ImmutableList::of,
                        (l1, l2) -> ImmutableList.<ThinArmorPiece>builder().addAll(l1).addAll(l2).build()
                ));

        return generateCombinations(Loadout.empty(), armorPiecesMap, nextArmorType(null)).stream().
                filter(l -> l.getArmorPieces().size() >= minimumPieces).
                collect(toList());
    }

    private List<Loadout> generateCombinations(Loadout currentLoadout, Map<ArmorType, List<ThinArmorPiece>> armorPiecesMap,
                                               ArmorType armorType)
    {
        List<ThinArmorPiece> currentArmorPieces = armorPiecesMap.getOrDefault(armorType, emptyList());
        if(hasNextArmorType(armorType)) {
            if(currentArmorPieces.isEmpty()) {
                return generateCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            }

            List<Loadout> loadoutsWithNextPiece = generateCombinations(currentLoadout, armorPiecesMap, nextArmorType(armorType));
            List<Loadout> loadoutsToReturn = new ArrayList<>(loadoutsWithNextPiece);
            for (Loadout loadoutWithNextPiece : loadoutsWithNextPiece) {
                for (ThinArmorPiece armorPiece : currentArmorPieces) {
                    loadoutsToReturn.add(Loadout.builder(loadoutWithNextPiece).
                            withArmorPiece(armorPiece).
                            build());
                }
            }
            return loadoutsToReturn;
        } else {
            List<Loadout> loadoutsToReturn = new ArrayList<>();
            loadoutsToReturn.add(currentLoadout);
            for (ThinArmorPiece armorPiece : currentArmorPieces) {
                loadoutsToReturn.add(Loadout.builder().
                        withArmorPiece(armorPiece).
                        build());
            }
            return loadoutsToReturn;
        }
    }
}
