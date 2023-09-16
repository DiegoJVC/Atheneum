package com.cobelpvp.atheneum.command;

import java.beans.ConstructorProperties;
import java.util.List;

public class FlagData implements Data {

    private final List<String> names;
    private final String description;
    private final boolean defaultValue;
    private final int methodIndex;

    @ConstructorProperties({"names", "description", "defaultValue", "methodIndex"})
    public FlagData(final List<String> names, final String description, final boolean defaultValue, final int methodIndex) {
        this.names = names;
        this.description = description;
        this.defaultValue = defaultValue;
        this.methodIndex = methodIndex;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    public List<String> getNames() {
        return this.names;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMethodIndex() {
        return this.methodIndex;
    }
}
