package com.app.ipfinder.storage;

import com.app.ipfinder.model.LookupEntry;

import java.util.List;

public interface HistoryStore {

    List<LookupEntry> load();

    void save(List<LookupEntry> entries);
}