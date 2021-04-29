package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPieceSkill;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DefenseScoringFunctionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void keyForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().build();
        scoringFunction.keyFor(null);
    }

    @Test
    public void scoreForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().build();
        scoringFunction.scoreFor(null);
    }

    private Loadout sampleLoadoutWith(int defense, int fireRes, int waterRes, int thunderRes, int iceRes,
                                      int dragonRes) {
       return sampleLoadoutWith(defense, fireRes, waterRes, thunderRes, iceRes, dragonRes, ImmutableMap.of());
    }

    private Loadout sampleLoadoutWith(int defense, int fireRes, int waterRes, int thunderRes, int iceRes, int dragonRes,
                                      Map<String, Integer> skillToLevelMap) {
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setArmorType(ArmorType.HEAD);
        armorPiece.setDefense(defense);
        armorPiece.setFireResistance(fireRes);
        armorPiece.setWaterResistance(waterRes);
        armorPiece.setThunderResistance(thunderRes);
        armorPiece.setIceResistance(iceRes);
        armorPiece.setDragonResistance(dragonRes);
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
        Loadout loadout = sampleLoadoutWith(
                100,
                1,
                1,
                1,
                1,
                1
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                withWaterResistanceWeight(5).
                withThunderResistanceWeight(7).
                withIceResistanceWeight(11).
                withDragonResistanceWeight(13).
                build();

        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Def:100;Fire Res:1;Water Res:1;Thunder Res:1;Ice Res:1;Dragon Res:1"));
        assertThat(scoringFunction.scoreFor(loadout), is(200.0 + 3 + 5 + 7 + 11 + 13));
        
    }

    @Test
    public void keyForAndScoreForShouldOnlyIncludeStatsThatHaveWeights() {
        Loadout loadout = sampleLoadoutWith(
                50,
                1,
                1,
                1,
                1,
                1
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withThunderResistanceWeight(7).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Thunder Res:1"));
        assertThat(scoringFunction.scoreFor(loadout), is(7.0));
    }

    @Test
    public void bucketSizesShouldBeAppliedCorrectly() {
        Loadout loadout = sampleLoadoutWith(
                100,
                8,
                19,
                0,
                0,
                0
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withDefenseBucketSize(11).
                withFireResistanceWeight(3).
                withWaterResistanceWeight(5).
                withResistanceBucketSize(7).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Def:99;Fire Res:7;Water Res:14"));
        assertThat(scoringFunction.scoreFor(loadout), is(198.0 + 21 + 70));
    }

    @Test
    public void defenseBoostSkillShouldBeFactoredInCorrectly() {
        Loadout loadout = sampleLoadoutWith(
                100,
                0,
                0,
                0,
                0,
                0,
                ImmutableMap.of(
                        "Defense Boost", 7
                )
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Def:145;Fire Res:5"));
        assertThat(scoringFunction.scoreFor(loadout), is(290.0 + 15));
    }

    @Test
    public void elementalResistanceSkillsShouldBeFactoredInCorrectly() {
        Loadout loadout = sampleLoadoutWith(
                100,
                0,
                0,
                0,
                0,
                0,
                ImmutableMap.of(
                        "Fire Resistance", 3
                )
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Def:110;Fire Res:20"));
        assertThat(scoringFunction.scoreFor(loadout), is(220.0 + 60));
    }

    @Test
    public void negativeResistanceMultiplierShouldBeFactoredInCorrectly() {
        Loadout loadout = sampleLoadoutWith(
                100,
                -1,
                0,
                0,
                0,
                0
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                withNegativeResistanceWeightMultiplier(4).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=Def:100;Fire Res:-1"));
        assertThat(scoringFunction.scoreFor(loadout), is(200.0 - 12));
    }

    @Test
    public void keyForShouldReturnEmptyKeyIfPerformanceModeIsSpeed() {
        Loadout loadout = sampleLoadoutWith(
                100,
                1,
                1,
                1,
                1,
                1
        );

        LoadoutScoringFunction scoringFunction = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                withWaterResistanceWeight(5).
                withThunderResistanceWeight(7).
                withIceResistanceWeight(11).
                withDragonResistanceWeight(13).
                withPerformanceMode(ScoringPerformanceMode.SPEED).
                build();
        
        assertThat(scoringFunction.keyFor(loadout), is("DefenseState=∅"));
        assertThat(scoringFunction.scoreFor(loadout), is(200.0 + 3 + 5 + 7 + 11 + 13));
    }

    @Test
    public void serializationAndDeserializationShouldNotChangeTheObject() throws IOException {
        LoadoutScoringFunction original = DefenseScoringFunction.builder().
                withDefenseWeight(2).
                withFireResistanceWeight(3).
                withWaterResistanceWeight(5).
                withThunderResistanceWeight(7).
                withIceResistanceWeight(11).
                withDragonResistanceWeight(13).
                build();

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedForm = objectMapper.writeValueAsString(original);
        LoadoutScoringFunction clone = objectMapper.readValue(serializedForm, DefenseScoringFunction.class);

        assertThat(original, is(equalTo(clone)));
    }
}