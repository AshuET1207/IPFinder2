package com.app.ipfinder.storage;

import com.app.ipfinder.config.IpFinderProperties;
import com.app.ipfinder.exception.HistoryStoreException;
import com.app.ipfinder.model.LookupEntry;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class JsonFileHistoryStore implements HistoryStore {

    private final ObjectMapper objectMapper;
    private final Path historyFile;

    public JsonFileHistoryStore(
            ObjectMapper objectMapper,
            IpFinderProperties properties) {
        this.objectMapper = objectMapper;
        this.historyFile = properties.history().file();
    }

    @Override
    public List<LookupEntry> load() {
        if (!Files.exists(historyFile)) {
            return List.of();
        }

        try {
            String json = Files.readString(historyFile);

            if (json.isBlank()) {
                return List.of();
            }

            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(
                                    List.class,
                                    LookupEntry.class));

        } catch (JacksonException e) {
            throw new HistoryStoreException(
                    "Could not read lookup history. The history file may be corrupted.",
                    e);

        } catch (IOException e) {
            throw new HistoryStoreException(
                    "Could not read lookup history.",
                    e);
        }
    }

    @Override
    public void save(List<LookupEntry> entries) {
        try {
            Path parent = historyFile.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            String json = objectMapper.writeValueAsString(entries);

            Path tempFile = historyFile.resolveSibling(
                    historyFile.getFileName() + ".tmp");

            Files.writeString(tempFile, json);

            Files.move(
                    tempFile,
                    historyFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);

        } catch (JacksonException e) {
            throw new HistoryStoreException(
                    "Could not serialize lookup history.",
                    e);

        } catch (IOException e) {
            throw new HistoryStoreException(
                    "Could not save lookup history.",
                    e);
        }
    }
}