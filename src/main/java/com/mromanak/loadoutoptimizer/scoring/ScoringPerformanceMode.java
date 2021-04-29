package com.mromanak.loadoutoptimizer.scoring;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum ScoringPerformanceMode {

    ACCURACY("Accuracy"),
    SPEED("Speed");

    private static final Map<String, ScoringPerformanceMode> nameToValueMap;

    static {
        ImmutableMap.Builder<String, ScoringPerformanceMode> nameToValueBuilder = ImmutableMap.builder();
        for(ScoringPerformanceMode selectorMode : values()) {
            nameToValueBuilder.put(selectorMode.getName(), selectorMode);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    ScoringPerformanceMode(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ScoringPerformanceMode forName(String name) {
        ScoringPerformanceMode performanceMode = nameToValueMap.get(name);
        if(performanceMode == null) {
            throw new IllegalArgumentException(
                    name + " is not a recognized scoring performance mode. Recognized scoring performance modes are: " + asList(values()));
        }
        return performanceMode;
    }
}
