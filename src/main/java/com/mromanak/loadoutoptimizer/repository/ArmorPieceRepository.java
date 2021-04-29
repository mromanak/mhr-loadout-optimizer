package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ArmorPieceRepository extends PagingAndSortingRepository<ArmorPiece, String> {

    Optional<ArmorPiece> findBySetNameAndArmorTypeAndSetType(String setName, ArmorType armorType, SetType setType);

    Set<ArmorPiece> findAllByNameIn(Set<String> names);

    Set<ArmorPiece> findBySetName(String setName);

    Set<ArmorPiece> findBySetNameAndSetType(String setName, SetType setType);

    @Query(
        "SELECT ap FROM ArmorPiece ap JOIN FETCH ap.skills aps JOIN FETCH aps.skill sk JOIN ap.skills aps_prime JOIN " +
            "aps_prime.skill sk_prime WHERE sk_prime.name IN (:skillNames)")
    Set<ArmorPiece> eagerFindBySkillNameIn(@Param("skillNames") Set<String> skillNames);
}
