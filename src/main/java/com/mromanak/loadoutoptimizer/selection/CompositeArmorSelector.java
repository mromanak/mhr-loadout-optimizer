package com.mromanak.loadoutoptimizer.selection;

import com.google.common.collect.ImmutableList;
import com.mromanak.loadoutoptimizer.model.dto.optimizer.ThinArmorPiece;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeArmorSelector implements ArmorSelector {
    private final List<ArmorSelector> selectors;

    private CompositeArmorSelector(Builder builder) {
        selectors = ImmutableList.copyOf(builder.selectors);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean test(ThinArmorPiece armorPiece) {
        Objects.requireNonNull(armorPiece, "armorPiece must be non-null");
        return selectors.stream().allMatch((rule) -> rule.test(armorPiece));
    }

    public static class Builder {
        private final List<ArmorSelector> selectors = new ArrayList<>();

        public Builder withSelectors(Collection<ArmorSelector> val) {
            Objects.requireNonNull(val, "selectors must be non-null");
            selectors.addAll(val);
            return this;
        }

        public Builder withSelector(ArmorSelector val) {
            Objects.requireNonNull(val, "selector must be non-null");
            selectors.add(val);
            return this;
        }

        public CompositeArmorSelector build() {
            return new CompositeArmorSelector(this);
        }
    }
}
