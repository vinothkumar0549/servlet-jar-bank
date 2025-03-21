package com.example.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleType {
    ADMIN("admin"),
    CUSTOMER("customer");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    @JsonValue  // Converts Enum to JSON as a string
    public String getValue() {
        return value;
    }

    @JsonCreator  // Validates input when deserializing
    public static RoleType fromString(String value) {
        for (RoleType role : RoleType.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}

