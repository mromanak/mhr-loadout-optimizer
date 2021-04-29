package com.mromanak.loadoutoptimizer.selection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static com.mromanak.loadoutoptimizer.selection.SelectorMode.*;

public class ArmorTypeSelectorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfArmorTypesIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorTypes must be non-null");
        new ArmorTypeSelector(null, EXCLUDE);
    }

    @Test
    public void constructorShouldThrowExceptionIfModeIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("mode must be non-null");
        new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD
        ), null);
    }

    @Test
    public void testShouldThrowExceptionIfThinArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD
        ), EXCLUDE);
        rule.test((ThinArmorPiece) null);
    }

    @Test
    public void testShouldThrowExceptionIfArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD
        ), EXCLUDE);
        rule.test((ArmorPiece) null);
    }

    @Test
    public void testShouldReturnTrueIfSetNameDoesNotMatchAnyExcludedSetName() {
        ArmorSelector rule = new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD,
                ArmorType.WAIST,
                ArmorType.LEGS
        ), EXCLUDE);
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setArmorType(ArmorType.ARMS);
        assertThat(rule.test(armorPiece), is(true));
        assertThat(rule.test(new ThinArmorPiece(armorPiece)), is(true));
    }

    @Test
    public void testShouldReturnFalseIfSetNameMatchesAnyExcludedSetName() {
        ArmorSelector rule = new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD,
                ArmorType.WAIST,
                ArmorType.LEGS
        ), EXCLUDE);
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setArmorType(ArmorType.LEGS);
        assertThat(rule.test(armorPiece), is(false));
        assertThat(rule.test(new ThinArmorPiece(armorPiece)), is(false));
    }

    @Test
    public void serializationAndDeserializationShouldNotChangeTheObject() throws IOException {
        ArmorSelector original = new ArmorTypeSelector(ImmutableSet.of(
                ArmorType.HEAD,
                ArmorType.WAIST,
                ArmorType.LEGS
        ), EXCLUDE);

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedForm = objectMapper.writeValueAsString(original);
        ArmorSelector clone = objectMapper.readValue(serializedForm, ArmorTypeSelector.class);

        assertThat(original, is(equalTo(clone)));
    }

    @Test
    public void youShouldWriteTestsForIncludeMode() {
        assertThat("You didn't write shit", is(equalTo("You wrote tests for include mode")));
    }
}
