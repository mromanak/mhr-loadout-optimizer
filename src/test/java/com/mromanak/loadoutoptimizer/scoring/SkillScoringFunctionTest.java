package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPieceSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import com.mromanak.loadoutoptimizer.scoring.SkillScoringFunction.SkillWeight;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SkillScoringFunctionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void keyForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(ImmutableList.of(
                        new SkillWeight("Earplugs", 5, 3)
                )).
                build();
        scoringFunction.keyFor(null);
    }

    @Test
    public void scoreForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(ImmutableList.of(
                        new SkillWeight("Earplugs", 5, 3)
                )).
                build();
        scoringFunction.scoreFor(null);
    }

    private Loadout sampleLoadoutWith(Map<String, Integer> skillToLevelMap) {
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setArmorType(ArmorType.HEAD);
        List<ArmorPieceSkill> skills = skillToLevelMap.entrySet().stream().
                map((Map.Entry<String, Integer> entry) -> {
                    Skill skill = new Skill();
                    skill.setName(entry.getKey());
                    return new ArmorPieceSkill(armorPiece, skill, entry.getValue());
                }).
                collect(Collectors.toList());
        armorPiece.setSkills(skills);
        ThinArmorPiece thinArmorPiece = new ThinArmorPiece(armorPiece);
        return Loadout.builder().
                withArmorPiece(thinArmorPiece).
                build();
    }

    @Test
    public void keyForAndScoreForShouldBeCalculatedCorrectly() {
        Loadout loadout = sampleLoadoutWith(ImmutableMap.of(
                "Earplugs", 1,
                "Health Boost", 2
        ));

        List<SkillWeight> skillWeights = ImmutableList.of(
                new SkillWeight("Earplugs", 5, 3),
                new SkillWeight("Health Boost", 3, 1)
        );
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(skillWeights).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("SkillState=Earplugs:1;Health Boost:2"));
        assertThat(scoringFunction.scoreFor(loadout), is(5.0));
    }

    @Test
    public void keyForAndScoreForShouldIgnoreSkillLevelsBeyondTheMaximum() {
        Loadout loadout = sampleLoadoutWith(ImmutableMap.of(
                "Earplugs", 6
        ));

        List<SkillWeight> skillWeights = ImmutableList.of(
                new SkillWeight("Earplugs", 5, 3)
        );
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(skillWeights).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("SkillState=Earplugs:5"));
        assertThat(scoringFunction.scoreFor(loadout), is(15.0));
    }

    @Test
    public void keyForAndScoreForShouldOnlyIncludeSkillsThatHaveWeightFunctions() {
        Loadout loadout = sampleLoadoutWith(ImmutableMap.of(
                "Earplugs", 1,
                "Health Boost", 2
        ));

        List<SkillWeight> skillWeights = ImmutableList.of(
                new SkillWeight("Earplugs", 5, 3),
                new SkillWeight("Attack Boost", 7, 1)
        );
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(skillWeights).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("SkillState=Earplugs:1"));
        assertThat(scoringFunction.scoreFor(loadout), is(3.0));
    }

    @Test
    public void keyForShouldReturnEmptyKeyIfPerformanceModeIsSpeed() {
        Loadout loadout = sampleLoadoutWith(ImmutableMap.of(
                "Earplugs", 1,
                "Health Boost", 2
        ));

        List<SkillWeight> skillWeights = ImmutableList.of(
                new SkillWeight("Earplugs", 5, 3),
                new SkillWeight("Health Boost", 3, 1)
        );
        LoadoutScoringFunction scoringFunction = SkillScoringFunction.builder().
                withSkillWeights(skillWeights).
                withPerformanceMode(ScoringPerformanceMode.SPEED).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("SkillState=∅"));
        assertThat(scoringFunction.scoreFor(loadout), is(5.0));
    }

    @Test
    public void serializationAndDeserializationShouldNotChangeTheObject() throws IOException {
        List<SkillWeight> skillWeights = ImmutableList.of(
                new SkillWeight("Earplugs", 5, 3),
                new SkillWeight("Attack Boost", 7, 1)
        );
        LoadoutScoringFunction original = SkillScoringFunction.builder().
                withSkillWeights(skillWeights).
                build();

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedForm = objectMapper.writeValueAsString(original);
        LoadoutScoringFunction clone = objectMapper.readValue(serializedForm, SkillScoringFunction.class);

        assertThat(original, is(equalTo(clone)));
    }

    @Test
    public void youShouldWriteTestsForEmptySkillWeights() {
        assertThat("You didn't write shit", is(equalTo("You wrote tests for the case where skillWeights is empty")));
    }
}