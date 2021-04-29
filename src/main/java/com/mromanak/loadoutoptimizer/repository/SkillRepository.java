package com.mromanak.loadoutoptimizer.repository;

import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SkillRepository extends PagingAndSortingRepository<Skill, String> {
}
