package mcmodutil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;

public class ConfigEntryHandle<T> {

    private static final Gson GSON = new Gson();

    private final String name;
    private final Class<T> type;

    private final T defaultValue;
    private T value;
    private boolean valueDifferentFromFile;

    private String description = null;

    public ConfigEntryHandle(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        value = defaultValue;
        valueDifferentFromFile = true;
        description = "(no description)";
    }

    public ConfigEntryHandle(String name, Class<T> type, T defaultValue, String description) {
        this(name, type, defaultValue);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void resetValue() {
        value = defaultValue;
        valueDifferentFromFile = true;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    // I should probably make a field for logger
    public void valueFromJsonElement(JsonElement raw, Logger logger) {
        try {
            value = GSON.fromJson(raw, type);
            valueDifferentFromFile = true;
        }
        catch (JsonSyntaxException e) {
            value = defaultValue;
            valueDifferentFromFile = true;
            logger.error("Entry \"{}\": cannot parse value to {}", name, type.getName());
        }
    }

    public void setValue(T value) {
        this.value = value;
        this.valueDifferentFromFile = true;
    }

    public T getValue() {
        return value;
    }

    public String getDescription() { return description; }

    public JsonElement toJsonElement() {
        return GSON.toJsonTree(value, type);
    }

    public void resetDifferentFromFile() {
        valueDifferentFromFile = false;
    }

    public boolean isValueDifferentFromFile(){
        return valueDifferentFromFile;
    }
}
