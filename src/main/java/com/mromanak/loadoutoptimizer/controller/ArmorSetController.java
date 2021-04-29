package com.mromanak.loadoutoptimizer.controller;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.dto.ArmorSetDto;
import com.mromanak.loadoutoptimizer.model.exception.BadRepositoryApiRequestException;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
import com.mromanak.loadoutoptimizer.service.DtoService;
import com.mromanak.loadoutoptimizer.utils.NameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Controller
@RequestMapping("/repository/armorSet")
public class ArmorSetController {

    private static final Map<String, SetType> ID_TO_SET_TYPE;

    static {
        ID_TO_SET_TYPE = ImmutableMap.copyOf(
            Arrays.stream(SetType.values()).
                collect(toMap((setType) -> NameUtils.toSlug(setType.getName()), identity()))
        );
    }

    private final ArmorPieceRepository repository;
    private final DtoService dtoService;

    public ArmorSetController(ArmorPieceRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<ArmorSetDto> updateArmorPieces(
        @RequestBody @Valid ArmorSetDto armorSetDto
    ) {
        repository.saveAll(dtoService.fromArmorSetDto(armorSetDto));
        return getArmorSet(armorSetDto.getSetName(), armorSetDto.getSetType());
    }

    private ResponseEntity<ArmorSetDto> getArmorSet(String setName, SetType setType) {
        Set<ArmorPiece> armorPieces = repository.findBySetNameAndSetType(setName, setType);
        return ResponseEntity.ok(dtoService.toArmorSetDto(armorPieces));
    }

    // TODO Clean up these endpoints to use completely sluggified URLs that can be derived intuitively from the DTOs
    // Or just delete them, I guess. Not like they represent a huge use case beyond debugging
    @RequestMapping(path = "/{setName}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<List<ArmorSetDto>> getArmorSets(
        @PathVariable String setName
    ) {
        List<ArmorSetDto> dtos = repository.findBySetName(setName).stream().
            collect(groupingBy(ArmorPiece::getSetType, toList())).
            values().
            stream().
            map(dtoService::toArmorSetDto).
            sorted(comparing(ArmorSetDto::getSetType)).
            collect(toList());
        return ResponseEntity.ok(dtos);
    }

    @RequestMapping(path = "/{setName}/{setTypeId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<ArmorSetDto> getArmorSet(
        @PathVariable String setName,
        @PathVariable String setTypeId
    ) {
        SetType setType = ID_TO_SET_TYPE.get(setTypeId);
        if (setType == null) {
            throw new BadRepositoryApiRequestException("ID " + setTypeId + " does not refer to a recognized set type." +
                " Valid IDs are " + Joiner.on(", ").join(ID_TO_SET_TYPE.values()));
        }

        ArmorSetDto dto = dtoService.toArmorSetDto(repository.findBySetNameAndSetType(setName, setType));
        return ResponseEntity.ok(dto);
    }
}
