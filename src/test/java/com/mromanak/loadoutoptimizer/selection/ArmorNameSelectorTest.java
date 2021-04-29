package com.mromanak.loadoutoptimizer.selection;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static com.mromanak.loadoutoptimizer.selection.SelectorMode.*;

public class ArmorNameSelectorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void constructorShouldThrowExceptionIfPatternsIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("patterns must be non-null");
        new ArmorNameSelector(null, EXCLUDE);
    }

    @Test
    public void constructorShouldThrowExceptionIfModeIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("mode must be non-null");
        new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^.*$")
        ), null);
    }

    @Test
    public void testShouldThrowExceptionIfThinArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^.*$")
        ), EXCLUDE);
        rule.test((ThinArmorPiece) null);
    }

    @Test
    public void testShouldThrowExceptionIfArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^.*$")
        ), EXCLUDE);
        rule.test((ArmorPiece) null);
    }

    @Test
    public void testShouldReturnTrueIfNameDoesNotMatchAnyExcludePattern() {
        ArmorSelector rule = new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^Pikachu Helm .*$"),
                Pattern.compile("^Pachirisu Helm .*$"),
                Pattern.compile("^Pikachu Helm .*$")
        ), EXCLUDE);
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setName("Emolga Helm α");
        assertThat(rule.test(armorPiece), is(true));
        assertThat(rule.test(new ThinArmorPiece(armorPiece)), is(true));
    }

    @Test
    public void testShouldReturnFalseIfNameMatchesAnyExcludePattern() {
        ArmorSelector rule = new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^Pikachu Helm .*$"),
                Pattern.compile("^Pachirisu Helm .*$"),
                Pattern.compile("^Pikachu Helm .*$")
        ), EXCLUDE);
        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setName("Pachirisu Helm α");
        assertThat(rule.test(armorPiece), is(false));
        assertThat(rule.test(new ThinArmorPiece(armorPiece)), is(false));
    }

    @Test
    public void serializationAndDeserializationShouldNotChangeTheObject() throws IOException {
        ArmorNameSelector original = new ArmorNameSelector(ImmutableList.of(
                Pattern.compile("^Pikachu Helm .*$"),
                Pattern.compile("^Pachirisu Helm .*$"),
                Pattern.compile("^Pikachu Helm .*$")
        ), EXCLUDE);

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedForm = objectMapper.writeValueAsString(original);
        ArmorNameSelector clone = objectMapper.readValue(serializedForm, ArmorNameSelector.class);

        List<String> originalPatterns = original.getPatterns().
                stream().
                map(Pattern::toString).
                collect(Collectors.toList());
        List<String> clonePatterns = clone.getPatterns().
                stream().
                map(Pattern::toString).
                collect(Collectors.toList());
        assertThat(originalPatterns, is(equalTo(clonePatterns)));
    }

    @Test
    public void youShouldWriteTestsForIncludeMode() {
        assertThat("You didn't write shit", is(equalTo("You wrote tests for include mode")));
    }
}
