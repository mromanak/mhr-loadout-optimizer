package com.mromanak.loadoutoptimizer.model.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static java.util.Arrays.asList;

public enum Rank {
    LOW_RANK("Low Rank"),
    HIGH_RANK("High Rank"),
    MASTER_RANK("Master Rank");

    private static final Map<String, Rank> nameToValueMap;

    static {
        ImmutableMap.Builder<String, Rank> nameToValueBuilder = ImmutableMap.builder();
        for(Rank rank : values()) {
            nameToValueBuilder.put(rank.getName(), rank);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    Rank(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public Rank forName(String name) {
        Rank rank = nameToValueMap.get(name);
        if(rank == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized rank. Recognized ranks are: " + asList(values()));
        }
        return rank;
    }
}
