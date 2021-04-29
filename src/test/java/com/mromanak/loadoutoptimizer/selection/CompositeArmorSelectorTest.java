package com.mromanak.loadoutoptimizer.selection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorPiece;
import com.mromanak.loadoutoptimizer.model.jpa.ArmorType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static com.mromanak.loadoutoptimizer.selection.SelectorMode.*;

public class CompositeArmorSelectorTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void withSelectorsShouldThrowExceptionIfSelectorIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("selector must be non-null");
        CompositeArmorSelector.builder().withSelector(null);
    }

    @Test
    public void withSelectorsShouldThrowExceptionIfSelectorsIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("selectors must be non-null");
        CompositeArmorSelector.builder().withSelectors(null);
    }

    @Test
    public void buildShouldThrowExceptionIfSelectorsContainsNull() {
        exceptionRule.expect(NullPointerException.class);
        List<ArmorSelector> selectors = new ArrayList<>();
        selectors.add(null);
        CompositeArmorSelector.builder().withSelectors(selectors).build();
    }

    @Test
    public void testShouldThrowExceptionIfThinArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = CompositeArmorSelector.builder().build();
        rule.test((ThinArmorPiece) null);
    }

    @Test
    public void testShouldThrowExceptionIfArmorPieceIsNull() {
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("armorPiece must be non-null");
        ArmorSelector rule = CompositeArmorSelector.builder().build();
        rule.test((ArmorPiece) null);
    }

    @Test
    public void testShouldReturnTrueIfNoRulesAreAdded() {
        ArmorSelector rule = CompositeArmorSelector.builder().build();
        ArmorPiece armorPiece = new ArmorPiece();
        assertThat(rule.test(armorPiece), is(true));
    }

    @Test
    public void testShouldReturnFalseIfAnyRuleExcludesArmorPiece() {
        ArmorSelector rule = CompositeArmorSelector.builder().
                withSelector(new ArmorNameSelector(ImmutableList.of(Pattern.compile("^Pikachu Helm.*$")), EXCLUDE)).
                withSelector(new ArmorTypeSelector(ImmutableSet.of(ArmorType.ARMS), EXCLUDE)).
                build();
        
        ArmorPiece armorPiece1 = new ArmorPiece();
        armorPiece1.setName("Pikachu Helm α");
        armorPiece1.setArmorType(ArmorType.HEAD);
        
        ArmorPiece armorPiece2 = new ArmorPiece();
        armorPiece2.setName("Pachirisu Braces α");
        armorPiece2.setArmorType(ArmorType.ARMS);
        
        assertThat(rule.test(armorPiece1), is(false));
        assertThat(rule.test(armorPiece2), is(false));
    }

    @Test
    public void testShouldReturnTrueIfAnyRuleExcludesArmorPiece() {
        ArmorSelector rule = CompositeArmorSelector.builder().
                withSelector(new ArmorNameSelector(ImmutableList.of(Pattern.compile("^Pikachu Helm.*$")), EXCLUDE)).
                withSelector(new ArmorTypeSelector(ImmutableSet.of(ArmorType.ARMS), EXCLUDE)).
                build();

        ArmorPiece armorPiece = new ArmorPiece();
        armorPiece.setName("Emolga Greaves α");
        armorPiece.setArmorType(ArmorType.LEGS);

        assertThat(rule.test(armorPiece), is(true));
    }
}
