package com.zevin.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapBuilder {
    private Map<String, Object> properties = new LinkedHashMap<>(16);
    
    public static MapBuilder object() {
        return new MapBuilder();
    }
    
    /**
     * 基本类型
     */
    public MapBuilder property(String key, Object value) {
        properties.put(key, value);
        return this;
    }
    
    /**
     * 对象类型
     */
    public MapBuilder property(String key, Supplier<MapBuilder> laziedValueBuilder) {
        properties.put(key, laziedValueBuilder.get());
        return this;
    }
    
    public Map<String, Object> build() {
        return properties;
    }
}
