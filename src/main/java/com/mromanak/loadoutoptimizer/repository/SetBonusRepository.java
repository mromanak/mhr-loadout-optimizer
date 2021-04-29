package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.SetBonus;
import com.mromanak.loadoutoptimizer.model.jpa.SetBonusSkill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SetBonusRepository extends PagingAndSortingRepository<SetBonus, String> {

    @Query("SELECT sbsk FROM SetBonusSkill sbsk JOIN FETCH sbsk.skill sk JOIN sbsk.skill sk_prime WHERE sk_prime.name = :skillName")
    Set<SetBonusSkill> eagerFindBySkillName(@Param("skillName") String skillName);
}
