package com.mromanak.loadoutoptimizer.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.jpa.Rank;
import com.mromanak.loadoutoptimizer.scoring.*;
import com.mromanak.loadoutoptimizer.selection.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@JsonDeserialize(builder = LoadoutRequest.Builder.class)
public class LoadoutRequest {

    private final Rank rank;
    private final List<String> setBonuses;

    private final ArmorTypeSelector armorTypeSelector;
    private final ArmorSetSelector armorSetSelector;
    private final ArmorNameSelector armorNameSelector;

    @JsonProperty("skillScoring")
    private final SkillScoringFunction skillScoringFunction;

    @JsonProperty("defenseScoring")
    private final DefenseScoringFunction defenseScoringFunction;

    @JsonProperty("decorationSlotScoring")
    private final DecorationSlotScoringFunction decorationSlotScoringFunction;

    @JsonProperty("sizeScoring")
    private final SizeScoringFunction sizeScoringFunction;

    private LoadoutRequest(Builder builder) {
        Objects.requireNonNull(builder.skillScoringFunction, "skillScoringFunction must be non-null");

        rank = builder.rank;
        setBonuses = ImmutableList.copyOf(builder.setBonuses);
        armorTypeSelector = builder.armorTypeSelector;
        armorSetSelector = builder.armorSetSelector;
        armorNameSelector = builder.armorNameSelector;
        skillScoringFunction = builder.skillScoringFunction;
        defenseScoringFunction = builder.defenseScoringFunction;
        decorationSlotScoringFunction = builder.decorationSlotScoringFunction;
        sizeScoringFunction = builder.sizeScoringFunction;
    }

    public ArmorSelector getCompositeSelector() {
        List<ArmorSelector> selectors = Stream.of(
                armorTypeSelector,
                armorSetSelector,
                armorNameSelector
        ).
                filter(Objects::nonNull).
                collect(Collectors.toList());
        return CompositeArmorSelector.builder().
                withSelectors(selectors).
                build();
    }

    public LoadoutScoringFunction getCompositeScoringFunction() {
        List<LoadoutScoringFunction> scoringFunctions = Stream.of(
                skillScoringFunction,
                defenseScoringFunction,
                decorationSlotScoringFunction,
                sizeScoringFunction
        ).
                filter(Objects::nonNull).
                collect(Collectors.toList());
        return CompositeScoringFunction.builder().
                withScoringFunctions(scoringFunctions).
                build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Rank rank = Rank.LOW_RANK;
        private List<String> setBonuses = new ArrayList<>();
        private ArmorTypeSelector armorTypeSelector;
        private ArmorSetSelector armorSetSelector;
        private ArmorNameSelector armorNameSelector;
        private SkillScoringFunction skillScoringFunction;
        private DefenseScoringFunction defenseScoringFunction;
        private DecorationSlotScoringFunction decorationSlotScoringFunction;
        private SizeScoringFunction sizeScoringFunction;

        private Builder() {
        }

        public Builder withRank(Rank val) {
            rank = (val == null) ? Rank.LOW_RANK : val;
            return this;
        }

        public Builder withSetBonuses(List<String> val) {
            setBonuses = (val == null) ? new ArrayList<>() : val;
            return this;
        }

        public Builder withArmorTypeSelector(ArmorTypeSelector val) {
            armorTypeSelector = val;
            return this;
        }

        public Builder withArmorSetSelector(ArmorSetSelector val) {
            armorSetSelector = val;
            return this;
        }

        public Builder withArmorNameSelector(ArmorNameSelector val) {
            armorNameSelector = val;
            return this;
        }

        @JsonProperty("skillScoring")
        public Builder withSkillScoringFunction(SkillScoringFunction val) {
            skillScoringFunction = val;
            return this;
        }

        @JsonProperty("defenseScoring")
        public Builder withDefenseScoringFunction(DefenseScoringFunction val) {
            defenseScoringFunction = val;
            return this;
        }

        @JsonProperty("decorationSlotScoring")
        public Builder withDecorationSlotScoringFunction(DecorationSlotScoringFunction val) {
            decorationSlotScoringFunction = val;
            return this;
        }

        @JsonProperty("sizeScoring")
        public Builder withSizeSlotScoringFunction(SizeScoringFunction val) {
            sizeScoringFunction = val;
            return this;
        }

        public LoadoutRequest build() {
            return new LoadoutRequest(this);
        }
    }
}
