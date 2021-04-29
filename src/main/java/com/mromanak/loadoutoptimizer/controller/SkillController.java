package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.dto.SkillDto;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.repository.SkillRepository;
import com.mromanak.loadoutoptimizer.service.DtoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Controller
@RequestMapping("/repository/skill")
public class SkillController {

    private final SkillRepository repository;
    private final DtoService dtoService;

    public SkillController(SkillRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<SkillDto> updateSkill(
        @RequestBody @Valid SkillDto skillDto,
        @RequestParam(defaultValue = "false") boolean preserveRelationships
    ) {
        repository.save(dtoService.fromDto(skillDto, preserveRelationships));
        return getSkill(skillDto.getId());
    }

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Page<SkillDto>> getSkills(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "25") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(repository.findAll(pageRequest).map(dtoService::toDto));
    }

    @RequestMapping(path = "/{skillId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<SkillDto> getSkill(
        @PathVariable String skillId
    ) {
        return repository.findById(skillId).
            map(dtoService::toDto).
            map(ResponseEntity::ok).
            orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + skillId));
    }

    @RequestMapping(path = "/{skillId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<SkillDto> deleteSkill(
        @PathVariable String skillId
    ) {
        repository.deleteById(skillId);
        return ResponseEntity.noContent().build();
    }
}
