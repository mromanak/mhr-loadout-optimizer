package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.dto.SetBonusDto;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.repository.SetBonusRepository;
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
@RequestMapping("/repository/setBonus")
public class SetBonusController {

    private final SetBonusRepository repository;
    private final DtoService dtoService;

    public SetBonusController(SetBonusRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<SetBonusDto> updateSetBonus(
        @RequestBody @Valid SetBonusDto setBonusDto
    ) {
        repository.save(dtoService.fromDto(setBonusDto));
        return getSetBonus(setBonusDto.getId());
    }

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Page<SetBonusDto>> getSetBonuss(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "25") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(repository.findAll(pageRequest).map(dtoService::toDto));
    }

    @RequestMapping(path = "/{setBonusId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<SetBonusDto> getSetBonus(
        @PathVariable String setBonusId
    ) {
        return repository.findById(setBonusId).
            map(dtoService::toDto).
            map(ResponseEntity::ok).
            orElseThrow(() -> new EntityNotFoundException("No setBonus found with ID " + setBonusId));
    }

    @RequestMapping(path = "/{setBonusId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<SetBonusDto> deleteSetBonus(
        @PathVariable String setBonusId
    ) {
        repository.deleteById(setBonusId);
        return ResponseEntity.noContent().build();
    }
}
