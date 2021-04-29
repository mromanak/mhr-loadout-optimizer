package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.dto.ArmorPieceDto;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
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
@RequestMapping("/repository/armorPiece")
public class ArmorPieceController {

    private final ArmorPieceRepository repository;
    private final DtoService dtoService;

    public ArmorPieceController(ArmorPieceRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<ArmorPieceDto> updateArmorPiece(
        @RequestBody @Valid ArmorPieceDto armorPieceDto,
        @RequestParam(defaultValue = "false") boolean preserveRelationships
    ) {
        repository.save(dtoService.fromDto(armorPieceDto, preserveRelationships));
        return getArmorPiece(armorPieceDto.getId());
    }

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Page<ArmorPieceDto>> getArmorPieces(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "25") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(repository.findAll(pageRequest).map(dtoService::toDto));
    }

    @RequestMapping(path = "/{armorPieceId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<ArmorPieceDto> getArmorPiece(
        @PathVariable String armorPieceId
    ) {
        return repository.findById(armorPieceId).
            map(dtoService::toDto).
            map(ResponseEntity::ok).
            orElseThrow(() -> new EntityNotFoundException("No armorPiece found with ID " + armorPieceId));
    }

    @RequestMapping(path = "/{armorPieceId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<ArmorPieceDto> deleteArmorPiece(
        @PathVariable String armorPieceId
    ) {
        repository.deleteById(armorPieceId);
        return ResponseEntity.noContent().build();
    }
}
