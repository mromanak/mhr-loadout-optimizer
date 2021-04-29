package com.mromanak.loadoutoptimizer.service;

import com.mromanak.loadoutoptimizer.model.dto.*;
import com.mromanak.loadoutoptimizer.model.exception.BadRepositoryApiRequestException;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.model.jpa.*;
import com.mromanak.loadoutoptimizer.repository.ArmorPieceRepository;
import com.mromanak.loadoutoptimizer.repository.JewelRepository;
import com.mromanak.loadoutoptimizer.repository.SetBonusRepository;
import com.mromanak.loadoutoptimizer.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.*;

@Service
public class DtoService {

    private final ArmorPieceRepository armorPieceRepository;
    private final JewelRepository jewelRepository;
    private final SetBonusRepository setBonusRepository;
    private final SkillRepository skillRepository;

    public DtoService(ArmorPieceRepository armorPieceRepository, JewelRepository jewelRepository,
        SetBonusRepository setBonusRepository, SkillRepository skillRepository) {
        this.armorPieceRepository = armorPieceRepository;
        this.jewelRepository = jewelRepository;
        this.setBonusRepository = setBonusRepository;
        this.skillRepository = skillRepository;
    }

    public SkillDto toDto(Skill skill) {
        if (skill == null) {
            return null;
        }

        SkillDto dto = new SkillDto();
        dto.setName(skill.getName());
        dto.setMaxLevel(skill.getMaxLevel());
        dto.setMaxUncappedLevel(skill.getMaxUncappedLevel());
        if (skill.getUncappingSkill() != null) {
            dto.setUncappingSkillId(skill.getUncappingSkill().getId());
        }
        dto.setDescription(skill.getDescription());
        dto.setEffects(getEffectDtos(skill));
        if (skill.getArmorPieces() != null) {
            dto.setArmorPieces(getArmorPieceDtos(skill));
        }
        if (skill.getJewels() != null) {
            dto.setJewels(getJewelDtos(skill));
        }
        if (skill.getSetBonuses() != null) {
            dto.setSetBonuses(getSetBonusDtos(skill));
        }
        return dto;
    }

    private TreeSet<SkillEffectDto> getEffectDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getEffects().entrySet().stream().
            map((entry) -> new SkillEffectDto(entry.getKey(), entry.getValue())).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillProviderDto> getArmorPieceDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getArmorPieces().stream().
            map((ArmorPieceSkill mapping) -> {
                return new SkillProviderDto(mapping.getPrimaryKey().getArmorPieceId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SkillProviderDto> getJewelDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getJewels().stream().
            map((JewelSkill mapping) -> {
                return new SkillProviderDto(mapping.getPrimaryKey().getJewelId(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private TreeSet<SetBonusSkillProviderDto> getSetBonusDtos(Skill skill) {
        if (skill == null) {
            return null;
        }

        return skill.getSetBonuses().stream().
            map((SetBonusSkill mapping) -> {
                return new SetBonusSkillProviderDto(mapping.getPrimaryKey().getSetBonusId(),
                    mapping.getRequiredPieces(), mapping.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    public Skill fromDto(SkillDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        Skill skill;
        if (preserveRelationships) {
            skill = skillRepository.findById(dto.getId()).orElseGet(Skill::new);
        } else {
            skill = new Skill();
        }
        skill.setName(dto.getName());
        skill.setMaxLevel(dto.getMaxLevel());
        skill.setMaxUncappedLevel(dto.getMaxUncappedLevel());
        skill.setDescription(dto.getDescription());
        skill.setEffects(getEffects(dto));

        if (!preserveRelationships) {
            if (dto.getUncappingSkillId() != null) {
                skill.setUncappingSkill(getUncappingSkill(dto.getUncappingSkillId()));
            }
            if (dto.getArmorPieces() != null) {
                skill.setArmorPieces(getArmorPieceSkills(skill, dto.getArmorPieces()));
            }
            if (dto.getJewels() != null) {
                skill.setJewels(getJewelSkills(skill, dto.getJewels()));
            }
            if (dto.getSetBonuses() != null) {
                skill.setSetBonuses(getSetBonusSkills(skill, dto.getSetBonuses()));
            }
        }
        return skill;
    }

    private Map<Integer, String> getEffects(SkillDto dto) {
        return dto.getEffects().stream().
            collect(toMap(SkillEffectDto::getLevel, SkillEffectDto::getDescription));
    }

    private Skill getUncappingSkill(String uncappedBy) {
        return skillRepository.findById(uncappedBy).
            orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + uncappedBy));
    }

    private List<ArmorPieceSkill> getArmorPieceSkills(Skill skill, Collection<SkillProviderDto> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map((SkillProviderDto spd) -> {
                ArmorPiece armorPiece = armorPieceRepository.findById(spd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No armor piece found with ID " + spd.getSourceId()));
                return new ArmorPieceSkill(armorPiece, skill, spd.getLevel());
            }).
            collect(toList());
    }

    private List<JewelSkill> getJewelSkills(Skill skill, Collection<SkillProviderDto> jewels) {
        if (jewels == null) {
            return null;
        }

        return jewels.stream().
            map((SkillProviderDto spd) -> {
                Jewel jewel = jewelRepository.findById(spd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No jewel found with ID " + spd.getSourceId()));
                return new JewelSkill(jewel, skill, spd.getLevel());
            }).
            collect(toList());
    }

    private List<SetBonusSkill> getSetBonusSkills(Skill skill, Collection<SetBonusSkillProviderDto> setBonuses) {
        if (setBonuses == null) {
            return null;
        }

        return setBonuses.stream().
            map((SetBonusSkillProviderDto sbspd) -> {
                SetBonus setBonus = setBonusRepository.findById(sbspd.getSourceId()).
                    orElseThrow(() -> new EntityNotFoundException("No set bonus found with ID " + sbspd.getSourceId()));
                return new SetBonusSkill(setBonus, skill, sbspd.getRequiredPieces(), sbspd.getLevel());
            }).
            collect(toList());
    }

    public JewelDto toDto(Jewel jewel) {
        if (jewel == null) {
            return null;
        }

        JewelDto dto = new JewelDto();
        dto.setName(jewel.getName());
        dto.setJewelLevel(jewel.getJewelLevel());
        dto.setRarity(jewel.getRarity());
        dto.setSkills(this.getJewelSkillDtos(jewel.getSkills()));
        return dto;
    }

    private SortedSet<ProvidedSkillDto> getJewelSkillDtos(Collection<JewelSkill> jewelSkills) {
        if (jewelSkills == null) {
            return null;
        }

        return jewelSkills.stream().
            map((js) -> new ProvidedSkillDto(js.getPrimaryKey().getSkillId(), js.getSkillLevel())).
            collect(toCollection(TreeSet::new));
    }

    public Jewel fromDto(JewelDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        Jewel jewel;
        if (preserveRelationships) {
            jewel = jewelRepository.findById(dto.getId()).orElseGet(Jewel::new);
        } else {
            jewel = new Jewel();
        }
        jewel.setName(dto.getName());
        jewel.setJewelLevel(dto.getJewelLevel());
        jewel.setRarity(dto.getRarity());

        if (!preserveRelationships) {
            jewel.setSkills(getJewelSkills(jewel, dto.getSkills()));
        }
        return jewel;
    }

    private List<JewelSkill> getJewelSkills(Jewel jewel, SortedSet<ProvidedSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((ProvidedSkillDto psd) -> {
                Skill skill = skillRepository.findById(psd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + psd.getSkillId()));
                return new JewelSkill(jewel, skill, psd.getLevel());
            }).
            collect(toList());
    }

    public ArmorPieceDto toDto(ArmorPiece armorPiece) {
        if (armorPiece == null) {
            return null;
        }

        ArmorPieceDto dto = new ArmorPieceDto();
        dto.setName(armorPiece.getName());
        dto.setSetName(armorPiece.getSetName());
        dto.setArmorType(armorPiece.getArmorType());
        dto.setSetType(armorPiece.getSetType());
        dto.setLevel1Slots(armorPiece.getLevel1Slots());
        dto.setLevel2Slots(armorPiece.getLevel2Slots());
        dto.setLevel3Slots(armorPiece.getLevel3Slots());
        dto.setLevel4Slots(armorPiece.getLevel4Slots());
        dto.setSkills(getArmorPieceSkillDtos(armorPiece.getSkills()));
        dto.setRarity(armorPiece.getRarity());
        dto.setDefense(armorPiece.getDefense());
        dto.setFireResistance(armorPiece.getFireResistance());
        dto.setWaterResistance(armorPiece.getWaterResistance());
        dto.setThunderResistance(armorPiece.getThunderResistance());
        dto.setIceResistance(armorPiece.getIceResistance());
        dto.setDragonResistance(armorPiece.getDragonResistance());
        if (armorPiece.getSetBonus() != null) {
            dto.setSetBonusId(armorPiece.getSetBonus().getId());
        }
        return dto;
    }

    private SortedSet<ProvidedSkillDto> getArmorPieceSkillDtos(Collection<ArmorPieceSkill> armorPieceSkills) {
        if (armorPieceSkills == null) {
            return null;
        }

        return armorPieceSkills.stream().
            map((aps) -> new ProvidedSkillDto(aps.getPrimaryKey().getSkillId(), aps.getSkillLevel())).
            collect(toCollection(TreeSet::new));
    }

    public ArmorPiece fromDto(ArmorPieceDto dto, boolean preserveRelationships) {
        if (dto == null) {
            return null;
        }

        ArmorPiece armorPiece;
        if (preserveRelationships) {
            armorPiece = armorPieceRepository.findById(dto.getId()).orElseGet(ArmorPiece::new);
        } else {
            armorPiece = new ArmorPiece();
        }
        armorPiece.setName(dto.getName());
        armorPiece.setSetName(dto.getSetName());
        armorPiece.setArmorType(dto.getArmorType());
        armorPiece.setSetType(dto.getSetType());
        armorPiece.setLevel1Slots(dto.getLevel1Slots());
        armorPiece.setLevel2Slots(dto.getLevel2Slots());
        armorPiece.setLevel3Slots(dto.getLevel3Slots());
        armorPiece.setLevel4Slots(dto.getLevel4Slots());
        armorPiece.setRarity(dto.getRarity());
        armorPiece.setDefense(dto.getDefense());
        armorPiece.setFireResistance(dto.getFireResistance());
        armorPiece.setWaterResistance(dto.getWaterResistance());
        armorPiece.setThunderResistance(dto.getThunderResistance());
        armorPiece.setIceResistance(dto.getIceResistance());
        armorPiece.setDragonResistance(dto.getDragonResistance());

        if (!preserveRelationships) {
            armorPiece.setSkills(getArmorPieceSkills(armorPiece, dto.getSkills()));

            if (dto.getSetBonusId() != null) {
                armorPiece.setSetBonus(getArmorPieceSetBonus(dto.getSetBonusId()));
            }

        }
        return armorPiece;
    }

    private List<ArmorPieceSkill> getArmorPieceSkills(ArmorPiece armorPiece, SortedSet<ProvidedSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((ProvidedSkillDto psd) -> {
                Skill skill = skillRepository.findById(psd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + psd.getSkillId()));
                return new ArmorPieceSkill(armorPiece, skill, psd.getLevel());
            }).
            collect(toList());
    }

    private SetBonus getArmorPieceSetBonus(String setBonusId) {
        return setBonusRepository.findById(setBonusId).
            orElseThrow(() -> new EntityNotFoundException("No set bonus found with ID " + setBonusId));
    }

    public SetBonusDto toDto(SetBonus setBonus) {
        if (setBonus == null) {
            return null;
        }

        SetBonusDto dto = new SetBonusDto();
        dto.setName(setBonus.getName());
        dto.setSkills(getSetBonusSkillDtos(setBonus.getSkills()));
        dto.setArmorPieces(getSetBonusArmorPieceDtos(setBonus.getArmorPieces()));
        return dto;
    }

    private SortedSet<SetBonusSkillDto> getSetBonusSkillDtos(Collection<SetBonusSkill> setBonusSkills) {
        if (setBonusSkills == null) {
            return null;
        }

        return setBonusSkills.stream().
            map((sbs) -> {
                return new SetBonusSkillDto(sbs.getPrimaryKey().getSkillId(), sbs.getRequiredPieces(),
                    sbs.getSkillLevel());
            }).
            collect(toCollection(TreeSet::new));
    }

    private SortedSet<String> getSetBonusArmorPieceDtos(Collection<ArmorPiece> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map(ArmorPiece::getId).
            collect(toCollection(TreeSet::new));
    }

    public SetBonus fromDto(SetBonusDto dto) {
        if (dto == null) {
            return null;
        }

        SetBonus setBonus = setBonusRepository.findById(dto.getId()).orElseGet(SetBonus::new);
        setBonus.setName(dto.getName());
        setBonus.setSkills(getSetBonusSkills(setBonus, dto.getSkills()));
        setBonus.setArmorPieces(getSetBonusArmorPieces(dto.getArmorPieces()));
        return setBonus;
    }

    private List<SetBonusSkill> getSetBonusSkills(SetBonus setBonus, SortedSet<SetBonusSkillDto> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream().
            map((SetBonusSkillDto sbsd) -> {
                Skill skill = skillRepository.findById(sbsd.getSkillId()).
                    orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + sbsd.getSkillId()));
                return new SetBonusSkill(setBonus, skill, sbsd.getRequiredPieces(), sbsd.getLevel());
            }).
            collect(toList());
    }

    private List<ArmorPiece> getSetBonusArmorPieces(SortedSet<String> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        return armorPieces.stream().
            map((String armorPieceId) -> {
                ArmorPiece armorPiece = armorPieceRepository.findById(armorPieceId).
                    orElseThrow(() -> new EntityNotFoundException("No armor piece found with ID " + armorPieceId));
                return armorPiece;
            }).
            collect(toList());
    }

    public ArmorSetDto toArmorSetDto(Collection<ArmorPiece> armorPieces) {
        if (armorPieces == null) {
            return null;
        }

        ArmorSetDto dto = mergeCommonFields(armorPieces);
        Map<ArmorType, ArmorSetComponentDto> armorPieceMap = mapArmorSetComponentDtos(armorPieces);
        dto.setHead(armorPieceMap.get(ArmorType.HEAD));
        dto.setBody(armorPieceMap.get(ArmorType.BODY));
        dto.setArms(armorPieceMap.get(ArmorType.ARMS));
        dto.setWaist(armorPieceMap.get(ArmorType.WAIST));
        dto.setLegs(armorPieceMap.get(ArmorType.LEGS));
        return dto;
    }

    private ArmorSetDto mergeCommonFields(Collection<ArmorPiece> armorPieces) {
        ArmorSetDto dto = new ArmorSetDto();
        Set<ArmorType> usedArmorTypes = new HashSet<>();
        String setName = null;
        SetType setType = null;
        String setBonusId = null;
        Integer rarity = null;
        Integer defense = null;
        Integer fireResistance = null;
        Integer waterResistance = null;
        Integer thunderResistance = null;
        Integer iceResistance = null;
        Integer dragonResistance = null;

        for (ArmorPiece armorPiece : armorPieces) {
            if (usedArmorTypes.contains(armorPiece.getArmorType())) {
                throw new BadRepositoryApiRequestException(
                    "An armor set cannot contain more than one armor piece with type " +
                        armorPiece.getArmorType().getName());
            } else {
                usedArmorTypes.add(armorPiece.getArmorType());
            }

            if (setName == null) {
                setName = armorPiece.getSetName();
            } else if (!Objects.equals(setName, armorPiece.getSetName())) {
                throw new BadRepositoryApiRequestException(
                    "An armor set must contain only armor pieces with the same set name.");
            }

            if (setType == null) {
                setType = armorPiece.getSetType();
            } else if (!Objects.equals(setType, armorPiece.getSetType())) {
                throw new BadRepositoryApiRequestException(
                    "An armor set must contain only armor pieces with the same set type.");
            }

            if (armorPiece.getSetBonus() != null) {
                String otherSetBonusId = armorPiece.getSetBonus().getId();
                if (setBonusId == null) {
                    setBonusId = otherSetBonusId;
                } else if (!Objects.equals(setBonusId, otherSetBonusId)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set must contain only armor pieces with the same set bonus.");
                }
            }

            if (armorPiece.getRarity() != null) {
                Integer otherRarity = armorPiece.getRarity();
                if (rarity == null) {
                    rarity = otherRarity;
                } else if (!Objects.equals(rarity, otherRarity)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set must contain only armor pieces with the same rarity.");
                }
            }

            if (armorPiece.getDefense() != null) {
                Integer otherDefense = armorPiece.getDefense();
                if (defense == null) {
                    defense = otherDefense;
                } else if (!Objects.equals(defense, otherDefense)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same defense.");
                }
            }

            if (armorPiece.getFireResistance() != null) {
                Integer otherFireResistance = armorPiece.getFireResistance();
                if (fireResistance == null) {
                    fireResistance = otherFireResistance;
                } else if (!Objects.equals(fireResistance, otherFireResistance)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same fire resistance.");
                }
            }

            if (armorPiece.getWaterResistance() != null) {
                Integer otherWaterResistance = armorPiece.getWaterResistance();
                if (waterResistance == null) {
                    waterResistance = otherWaterResistance;
                } else if (!Objects.equals(waterResistance, otherWaterResistance)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same water resistance.");
                }
            }

            if (armorPiece.getThunderResistance() != null) {
                Integer otherThunderResistance = armorPiece.getThunderResistance();
                if (thunderResistance == null) {
                    thunderResistance = otherThunderResistance;
                } else if (!Objects.equals(thunderResistance, otherThunderResistance)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same thunder resistance.");
                }
            }

            if (armorPiece.getIceResistance() != null) {
                Integer otherIceResistance = armorPiece.getIceResistance();
                if (iceResistance == null) {
                    iceResistance = otherIceResistance;
                } else if (!Objects.equals(iceResistance, otherIceResistance)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same ice resistance.");
                }
            }

            if (armorPiece.getDragonResistance() != null) {
                Integer otherDragonResistance = armorPiece.getDragonResistance();
                if (dragonResistance == null) {
                    dragonResistance = otherDragonResistance;
                } else if (!Objects.equals(dragonResistance, otherDragonResistance)) {
                    throw new BadRepositoryApiRequestException(
                        "An armor set is assumed to contain only armor pieces with the same dragon resistance.");
                }
            }
        }
        dto.setSetName(setName);
        dto.setSetType(setType);
        dto.setSetBonusId(setBonusId);
        dto.setRarity(rarity);
        dto.setDefense(defense);
        dto.setFireResistance(fireResistance);
        dto.setWaterResistance(waterResistance);
        dto.setThunderResistance(thunderResistance);
        dto.setIceResistance(iceResistance);
        dto.setDragonResistance(dragonResistance);

        return dto;
    }

    private Map<ArmorType, ArmorSetComponentDto> mapArmorSetComponentDtos(Collection<ArmorPiece> armorPieces) {
        Map<ArmorType, ArmorSetComponentDto> map = new HashMap<>();
        for (ArmorPiece armorPiece : armorPieces) {
            ArmorSetComponentDto dto = toArmorSetComponentDto(armorPiece);
            if (map.put(armorPiece.getArmorType(), dto) != null) {
                throw new BadRepositoryApiRequestException(
                    "An armor set cannot contain more than one armor piece with type " +
                        armorPiece.getArmorType().getName());
            }
        }
        return map;
    }

    private ArmorSetComponentDto toArmorSetComponentDto(ArmorPiece armorPiece) {
        ArmorSetComponentDto dto = new ArmorSetComponentDto();
        dto.setName(armorPiece.getName());
        dto.setLevel1Slots(armorPiece.getLevel1Slots());
        dto.setLevel2Slots(armorPiece.getLevel2Slots());
        dto.setLevel3Slots(armorPiece.getLevel3Slots());
        dto.setLevel4Slots(armorPiece.getLevel4Slots());
        dto.setSkills(getArmorPieceSkillDtos(armorPiece.getSkills()));
        return dto;
    }

    public List<ArmorPiece> fromArmorSetDto(ArmorSetDto dto) {
        List<ArmorPiece> armorPieces = new ArrayList<>();
        SetBonus setBonus = null;
        if (dto.getSetBonusId() != null) {
            setBonus = getArmorPieceSetBonus(dto.getSetBonusId());
        }

        if (dto.getHead() != null) {
            armorPieces.add(fromArmorSetComponentDto(dto, dto.getHead(), ArmorType.HEAD, setBonus));
        }

        if (dto.getBody() != null) {
            armorPieces.add(fromArmorSetComponentDto(dto, dto.getBody(), ArmorType.BODY, setBonus));
        }

        if (dto.getArms() != null) {
            armorPieces.add(fromArmorSetComponentDto(dto, dto.getArms(), ArmorType.ARMS, setBonus));
        }

        if (dto.getWaist() != null) {
            armorPieces.add(fromArmorSetComponentDto(dto, dto.getWaist(), ArmorType.WAIST, setBonus));
        }

        if (dto.getLegs() != null) {
            armorPieces.add(fromArmorSetComponentDto(dto, dto.getLegs(), ArmorType.LEGS, setBonus));
        }

        return armorPieces;
    }

    private ArmorPiece fromArmorSetComponentDto(ArmorSetDto setDto, ArmorSetComponentDto dto, ArmorType armorType,
        SetBonus setBonus)
    {
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setName(dto.getName());
        armorPiece.setSetName(setDto.getSetName());
        armorPiece.setArmorType(armorType);
        armorPiece.setSetType(setDto.getSetType());
        armorPiece.setLevel1Slots(dto.getLevel1Slots());
        armorPiece.setLevel2Slots(dto.getLevel2Slots());
        armorPiece.setLevel3Slots(dto.getLevel3Slots());
        armorPiece.setLevel4Slots(dto.getLevel4Slots());
        armorPiece.setSkills(getArmorPieceSkills(armorPiece, dto.getSkills()));
        armorPiece.setSetBonus(setBonus);
        armorPiece.setRarity(setDto.getRarity());
        armorPiece.setDefense(setDto.getDefense());
        armorPiece.setFireResistance(setDto.getFireResistance());
        armorPiece.setWaterResistance(setDto.getWaterResistance());
        armorPiece.setThunderResistance(setDto.getThunderResistance());
        armorPiece.setIceResistance(setDto.getIceResistance());
        armorPiece.setDragonResistance(setDto.getDragonResistance());
        return armorPiece;
    }
}
