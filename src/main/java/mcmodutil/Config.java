package mcmodutil;

import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

public abstract class Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Logger logger;
    private final File folder;
    private final File configFile;
    private final File docFile;
    private JsonObject jsonRoot;
    private final HashMap<String, JsonElement> jsonEntries = new HashMap<>();
    private final HashMap<String, ConfigEntryHandle<?>> entryHandles = new HashMap<>();

    private String doc = null;
    private boolean hasDoc = false;

    public Config(Logger logger, boolean useSubfolder) {
        this.logger = logger;

        if (!useSubfolder) folder = Paths.get(System.getProperty("user.dir"), "config").toFile();
        else folder = Paths.get(System.getProperty("user.dir"), "config", logger.getName()).toFile();

        configFile = Path.of(folder.getPath(), logger.getName() + ".json").toFile();
        docFile = Path.of(folder.getPath(), logger.getName() + "_doc.txt").toFile();
    }

    public Config(Logger logger, boolean useSubfolder, String documentation) {
        this(logger, useSubfolder);
        this.doc = documentation;
        hasDoc = documentation == null;
    }

    protected void registerEntryHandle(ConfigEntryHandle<?> entry) {
        entryHandles.put(entry.getName(), entry);
        if (entry.getDescription() != null) hasDoc = true;
    }

    public void load() {
        if (!configFile.exists()) {
            logger.error("Can't load config: file is missing.");
            return;
        }

        jsonEntries.clear();
        String content;
        try {
            content = new String(Files.readAllBytes(configFile.toPath()));
        }
        catch (IOException e) {
            logger.error("Can't load config: {}", e.getMessage());
            return;
        }
        try {
            jsonRoot = JsonParser.parseString(content).getAsJsonObject();
        }
        catch (IllegalStateException e) {
            logger.error("Error loading config: {}", e.getMessage());
            jsonRoot = null;
            return;
        }

        for (Map.Entry<String, JsonElement> pair : jsonRoot.entrySet()) {
            jsonEntries.put(pair.getKey(), pair.getValue());
        }

        for (Map.Entry<String, ConfigEntryHandle<?>> pair : entryHandles.entrySet()) {
            JsonElement entry = jsonEntries.get(pair.getKey());
            ConfigEntryHandle<?> handle = pair.getValue();
            if (entry == null) handle.resetValue();
            else {
                handle.valueFromJsonElement(entry, logger);
                handle.resetDifferentFromFile();
            }
        }
    }

    public void save() {
        if (jsonRoot == null) jsonRoot = new JsonObject();

        for (Map.Entry<String, ConfigEntryHandle<?>> pair : entryHandles.entrySet()) {
            ConfigEntryHandle<?> handle = pair.getValue();
            if (!handle.isValueDifferentFromFile()) continue;
            String name = pair.getKey();
            if (jsonEntries.get(name) != null) jsonRoot.remove(name);
            JsonElement json = handle.toJsonElement();
            jsonRoot.add(name, json);
            jsonEntries.put(name, json);
            handle.resetDifferentFromFile();
        }

        if (!folder.exists()) {
            try {
                Files.createDirectories(folder.toPath());
            }
            catch (IOException e) {
                logger.error("Can't create config directory: {}", e.getMessage());
                return;
            }
        }

        boolean configFileExists;
        if (!configFile.exists()) {
            try {
                Files.createFile(configFile.toPath());
                configFileExists = true;
            }
            catch (IOException e) {
                logger.error("Can't save config: {}", e.getMessage());
                configFileExists = false;
            }
        }
        else configFileExists = true;

        if (configFileExists) {
            try (FileWriter fileWriter = new FileWriter(configFile)) {
                GSON.toJson(jsonRoot, fileWriter);
            }
            catch (IOException e) {
                logger.error("Can't save config: {}", e.getMessage());
            }
        }

        if (hasDoc && !docFile.exists()) {
            try {
                Files.createFile(docFile.toPath());
                FileWriter fileWriter = new FileWriter(docFile);
                if (doc != null) {
                    fileWriter.write(doc);
                    fileWriter.append("\n\n");
                }
                for (ConfigEntryHandle<?> entry : entryHandles.values()) {
                    String desc = entry.getDescription();
                    if (desc == null) continue;
                    fileWriter.write("=== " + entry.getName() + " (" + entry.getDefaultValue().getClass().getSimpleName() + ") ===\n");
                    fileWriter.write(desc);
                    fileWriter.write("\n\n");
                }
                fileWriter.close();
            }
            catch (IOException e) {
                logger.error("Can't save config documentation: {}", e.getMessage());
            }
        }
    }

    public boolean isLoaded() {
        return jsonRoot == null;
    }
}
