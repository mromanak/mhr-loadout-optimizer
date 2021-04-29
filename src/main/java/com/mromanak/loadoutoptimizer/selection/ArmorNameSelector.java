package com.mromanak.loadoutoptimizer.selection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
public class ArmorNameSelector implements ArmorSelector {

    private final List<Pattern> patterns;
    private final SelectorMode mode;

    @JsonCreator
    public ArmorNameSelector(@JsonProperty("patterns") Collection<Pattern> patterns,
                             @JsonProperty("mode") SelectorMode mode) {
        Objects.requireNonNull(patterns, "patterns must be non-null");
        Objects.requireNonNull(mode, "mode must be non-null");
        this.patterns = ImmutableList.copyOf(patterns);
        this.mode = mode;
    }

    @Override
    public boolean test(ThinArmorPiece armorPiece) {
        Objects.requireNonNull(armorPiece, "armorPiece must be non-null");
        switch(mode) {
            case INCLUDE:
                return patterns.stream().anyMatch(p -> p.matcher(armorPiece.getName()).matches());
            case EXCLUDE:
            default:
                return patterns.stream().noneMatch(p -> p.matcher(armorPiece.getName()).matches());
        }

    }
}
