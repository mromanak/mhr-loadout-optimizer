package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.dto.JewelDto;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.repository.JewelRepository;
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
@RequestMapping("/repository/jewel")
public class JewelController {

    private final JewelRepository repository;
    private final DtoService dtoService;

    public JewelController(JewelRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<JewelDto> updateJewel(
        @RequestBody @Valid JewelDto jewelDto,
        @RequestParam(defaultValue = "false") boolean preserveRelationships
    ) {
        repository.save(dtoService.fromDto(jewelDto, preserveRelationships));
        return getJewel(jewelDto.getId());
    }

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Page<JewelDto>> getJewels(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "25") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(repository.findAll(pageRequest).map(dtoService::toDto));
    }

    @RequestMapping(path = "/{jewelId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<JewelDto> getJewel(
        @PathVariable String jewelId
    ) {
        return repository.findById(jewelId).
            map(dtoService::toDto).
            map(ResponseEntity::ok).
            orElseThrow(() -> new EntityNotFoundException("No jewel found with ID " + jewelId));
    }

    @RequestMapping(path = "/{jewelId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<JewelDto> deleteJewel(
        @PathVariable String jewelId
    ) {
        repository.deleteById(jewelId);
        return ResponseEntity.noContent().build();
    }
}
