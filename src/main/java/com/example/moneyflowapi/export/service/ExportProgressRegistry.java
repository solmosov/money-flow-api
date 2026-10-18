package com.example.moneyflowapi.export.service;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ExportProgressRegistry {

    public record Progress(long total, AtomicLong processed) {
    }

    private final ConcurrentMap<UUID, Progress> map = new ConcurrentHashMap<>();

    public Progress start(UUID id, long total) {
        Progress progress = new Progress(total, new AtomicLong());
        map.put(id, progress);
        return progress;
    }

    public Progress get(UUID id) {
        return map.get(id);
    }

    public void remove(UUID id) {
        map.remove(id);
    }
}
