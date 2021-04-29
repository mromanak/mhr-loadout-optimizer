package com.mromanak.loadoutoptimizer.model.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.mromanak.loadoutoptimizer.model.jpa.Rank.*;
import static java.util.Arrays.asList;

public enum SetType {

    NONE("LR", LOW_RANK),
    S("S", HIGH_RANK);

    private final String name;
    private final Rank rank;

    private static final Map<String, SetType> nameToValueMap;

    static {
        ImmutableMap.Builder<String, SetType> nameToValueBuilder = ImmutableMap.builder();
        for(SetType setType : values()) {
            nameToValueBuilder.put(setType.getName(), setType);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    SetType(String name, Rank rank) {
        this.name = name;
        this.rank = rank;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public Rank getRank() {
        return rank;
    }

    @JsonCreator
    public static SetType forName(String name) {
        SetType setType = nameToValueMap.get(name);
        if(setType == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized set type. Recognized set types are: " + nameToValueMap.keySet());
        }
        return setType;
    }
}
