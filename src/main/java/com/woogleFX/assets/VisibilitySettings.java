package com.woogleFX.assets;

import java.util.*;

public class VisibilitySettings {

    private final Map<String, Integer> visibilityStatusMap = new HashMap<>();
    private final Map<String, Integer> visibilityMaxMap = new HashMap<>();
    private final List<String> keysInOrder = new ArrayList<>();

    public String[] getVisibilityKeys() {
        return keysInOrder.toArray(String[]::new);
    }

    public int getVisibilityStatus(String key) {
        return visibilityStatusMap.getOrDefault(key, 1);
    }

    public void addVisibilityStatus(String key, int max) {
        addVisibilityStatus(key, max, max);
    }


    public void addVisibilityStatus(String key, int initialValue, int max) {
        visibilityStatusMap.put(key, initialValue);
        visibilityMaxMap.put(key, max);
        keysInOrder.add(key);
    }

    public void decrementVisibilityStatus(String key) {
        if (visibilityStatusMap.get(key) == 0) {
            visibilityStatusMap.put(key, visibilityMaxMap.get(key));
        } else {
            visibilityStatusMap.put(key, visibilityStatusMap.get(key) - 1);
        }
    }

}
