package com.app.ipfinder.service;

import com.app.ipfinder.config.IpFinderProperties;
import com.app.ipfinder.model.LookupEntry;
import com.app.ipfinder.storage.HistoryStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {

    private final HistoryStore historyStore;
    private final int maxEntries;

    private final List<LookupEntry> entries;

    public HistoryService(
            HistoryStore historyStore,
            IpFinderProperties properties
    ) {
        this.historyStore = historyStore;
        this.maxEntries = properties.history().maxEntries();
        this.entries = new ArrayList<>(historyStore.load());

        trimToLimit();
    }

    public synchronized List<LookupEntry> getAll() {
        return List.copyOf(entries);
    }

    public synchronized void add(LookupEntry entry) {
        entries.add(0, entry);

        trimToLimit();
        historyStore.save(entries);
    }

    public synchronized void clear() {
        entries.clear();
        historyStore.save(entries);
    }

    private void trimToLimit() {
        if (entries.size() <= maxEntries) {
            return;
        }

        entries.subList(maxEntries, entries.size()).clear();
    }
}
