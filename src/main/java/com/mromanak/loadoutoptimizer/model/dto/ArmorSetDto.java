package com.mromanak.loadoutoptimizer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mromanak.loadoutoptimizer.model.jpa.SetType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(
    {"setName", "setType", "setBonusId", "level1Slots", "level2Slots", "level3Slots", "level4Slots", "rarity",
        "defense", "fireResistance", "waterResistance", "thunderResistance", "iceResistance", "dragonResistance",
        "skills", "head", "body", "arms", "waist", "legs", "charm"})
public class ArmorSetDto {

    @NotBlank(message = "Set name must be non-null")
    private String setName;

    @NotNull(message = "Set type must be non-null")
    private SetType setType;

    private String setBonusId;

    @NotNull(message = "Rarity must be non-null")
    @Min(value = 1, message = "Rarity must be at least 1")
    private Integer rarity;

    @NotNull(message = "Defense must be non-null")
    @Min(value = 0, message = "Defense must be non-negative")
    private Integer defense = 0;

    @NotNull(message = "Fire resistance must be non-null")
    private Integer fireResistance = 0;

    @NotNull(message = "Water resistance must be non-null")
    private Integer waterResistance = 0;

    @NotNull(message = "Thunder resistance must be non-null")
    private Integer thunderResistance = 0;

    @NotNull(message = "Ice resistance must be non-null")
    private Integer iceResistance = 0;

    @NotNull(message = "Dragon resistance must be non-null")
    private Integer dragonResistance = 0;

    @Valid
    private ArmorSetComponentDto head;

    @Valid
    private ArmorSetComponentDto body;

    @Valid
    private ArmorSetComponentDto arms;

    @Valid
    private ArmorSetComponentDto waist;

    @Valid
    private ArmorSetComponentDto legs;

    @Valid
    private ArmorSetComponentDto charm;

    @JsonIgnore
    public List<ArmorSetComponentDto> getArmorSetComponents() {
        return Stream.of(head, body, arms, legs, waist).
            filter(Objects::nonNull).
            collect(toList());
    }

    public int getLevel1Slots() {
        return getArmorSetComponents().stream().
            filter(Objects::nonNull).
            mapToInt(ArmorSetComponentDto::getLevel1Slots).
            sum();
    }

    public int getLevel2Slots() {
        return getArmorSetComponents().stream().
            filter(Objects::nonNull).
            mapToInt(ArmorSetComponentDto::getLevel2Slots).
            sum();
    }

    public int getLevel3Slots() {
        return getArmorSetComponents().stream().
            mapToInt(ArmorSetComponentDto::getLevel3Slots).
            filter(Objects::nonNull).
            sum();
    }

    public int getLevel4Slots() {
        return getArmorSetComponents().stream().
            mapToInt(ArmorSetComponentDto::getLevel4Slots).
            filter(Objects::nonNull).
            sum();
    }

    public SortedSet<ProvidedSkillDto> getSkills() {
        Collection<ProvidedSkillDto> set = getArmorSetComponents().stream().
            filter(Objects::nonNull).
            map(ArmorSetComponentDto::getSkills).
            filter(Objects::nonNull).
            flatMap(Collection::stream).
            collect(toMap(ProvidedSkillDto::getSkillId, Function.identity(),
                (psd1, psd2) -> new ProvidedSkillDto(psd1.getSkillId(), psd1.getLevel() + psd2.getLevel())))
            .values();
        return new TreeSet<>(set);
    }
}
