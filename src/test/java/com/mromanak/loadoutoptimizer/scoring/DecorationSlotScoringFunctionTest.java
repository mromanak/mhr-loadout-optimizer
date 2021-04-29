package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mromanak.loadoutoptimizer.model.Loadout;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DecorationSlotScoringFunctionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void keyForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = DecorationSlotScoringFunction.builder().build();
        scoringFunction.keyFor(null);
    }

    @Test
    public void scoreForShouldThrowExceptionIfLoadoutIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("loadout must be non-null");
        LoadoutScoringFunction scoringFunction = DecorationSlotScoringFunction.builder().build();
        scoringFunction.scoreFor(null);
    }

    private Loadout sampleLoadoutWith(int level1Slots, int level2Slots, int level3Slots, int level4Slots) {
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setArmorType(ArmorType.HEAD);
        armorPiece.setLevel1Slots(level1Slots);
        armorPiece.setLevel2Slots(level2Slots);
        armorPiece.setLevel3Slots(level3Slots);
        armorPiece.setLevel4Slots(level4Slots);
        ThinArmorPiece thinArmorPiece = new ThinArmorPiece(armorPiece);
        return Loadout.builder().
                withArmorPiece(thinArmorPiece).
                build();
    }

    @Test
    public void keyForAndScoreForShouldBeCalculatedCorrectly() {
        Loadout loadout = sampleLoadoutWith(
                1,
                1,
                1,
                1
        );

        LoadoutScoringFunction scoringFunction = DecorationSlotScoringFunction.builder().
                withLevel1SlotWeight(1).
                withLevel2SlotWeight(2).
                withLevel3SlotWeight(3).
                withLevel4SlotWeight(4).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("DecorationSlotState=Lv1:1;Lv2:1;Lv3:1;Lv4:1"));
        assertThat(scoringFunction.scoreFor(loadout), is(1.0 + 2 + 3 + 4));
    }

    @Test
    public void keyShouldOnlyIncludeSlotsThatHaveWeights() {
        Loadout loadout = sampleLoadoutWith(
                1,
                3,
                2,
                1
        );

        LoadoutScoringFunction scoringFunction = DecorationSlotScoringFunction.builder().
                withLevel1SlotWeight(1).
                withLevel4SlotWeight(4).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("DecorationSlotState=Lv1:1;Lv4:1"));
        assertThat(scoringFunction.scoreFor(loadout), is(1.0 + 4));
    }

    @Test
    public void keyForShouldReturnEmptyKeyIfPerformanceModeIsSpeed() {
        Loadout loadout = sampleLoadoutWith(
                0,
                1,
                1,
                0
        );

        LoadoutScoringFunction scoringFunction = DecorationSlotScoringFunction.builder().
                withLevel1SlotWeight(1).
                withLevel2SlotWeight(2).
                withLevel3SlotWeight(3).
                withLevel4SlotWeight(4).
                withPerformanceMode(ScoringPerformanceMode.SPEED).
                build();

        assertThat(scoringFunction.keyFor(loadout), is("DecorationSlotState=∅"));
        assertThat(scoringFunction.scoreFor(loadout), is(2.0 + 3));
    }

    @Test
    public void serializationAndDeserializationShouldNotChangeTheObject() throws IOException {
        LoadoutScoringFunction original = DecorationSlotScoringFunction.builder().
                withLevel1SlotWeight(1).
                withLevel2SlotWeight(2).
                withLevel3SlotWeight(3).
                withLevel4SlotWeight(4).
                build();

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedForm = objectMapper.writeValueAsString(original);
        LoadoutScoringFunction clone = objectMapper.readValue(serializedForm, DecorationSlotScoringFunction.class);

        assertThat(original, is(equalTo(clone)));
    }
}