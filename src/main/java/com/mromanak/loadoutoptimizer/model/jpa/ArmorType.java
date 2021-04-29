package com.mromanak.loadoutoptimizer.model.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;

public enum ArmorType {
    HEAD("Head"),
    BODY("Body"),
    ARMS("Arms"),
    WAIST("Waist"),
    LEGS("Legs");

    private static final Map<String, ArmorType> nameToValueMap;

    static {
        ImmutableMap.Builder<String, ArmorType> nameToValueBuilder = ImmutableMap.builder();
        for(ArmorType armorType : values()) {
            nameToValueBuilder.put(armorType.getName(), armorType);
        }
        nameToValueMap = nameToValueBuilder.build();
    }

    private final String name;

    ArmorType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ArmorType forName(String name) {
        ArmorType armorType = nameToValueMap.get(name);
        if(armorType == null) {
            throw new IllegalArgumentException(
                name + " is not a recognized armor type. Recognized armor types are: " + asList(values()));
        }
        return armorType;
    }

    public static boolean hasNextArmorType(ArmorType armorType) {
        return armorType != ArmorType.LEGS;
    }

    public static ArmorType nextArmorType(
        ArmorType armorType) {
        if(armorType == null) {
            return ArmorType.HEAD;
        }

        switch(armorType) {
            case HEAD:
                return ArmorType.BODY;
            case BODY:
                return ArmorType.ARMS;
            case ARMS:
                return ArmorType.WAIST;
            case WAIST:
                return ArmorType.LEGS;
            case LEGS:
            default:
                throw new NoSuchElementException();
        }
    }
}
