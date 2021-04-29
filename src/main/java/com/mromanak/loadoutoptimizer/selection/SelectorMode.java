package com.mromanak.loadoutoptimizer.selection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum SelectorMode {
    INCLUDE("Include"),
    EXCLUDE("Exclude");

    private static final Map<String, SelectorMode> nameToValueMap;

    static {
        ImmutableMap.Builder<String, SelectorMode> nameToValueBuilder = ImmutableMap.builder();
        for(SelectorMode selectorMode : values()) {
            nameToValueBuilder.put(selectorMode.getName(), selectorMode);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    SelectorMode(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static SelectorMode forName(String name) {
        SelectorMode selectorMode = nameToValueMap.get(name);
        if(selectorMode == null) {
            throw new IllegalArgumentException(
                    name + " is not a recognized selector mode. Recognized selector modes are: " + asList(values()));
        }
        return selectorMode;
    }
}
