package com.zevin.serialization.typehandlers;

import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Converts each map entry value; keys are stringified. */
public class MapHandler implements TypeHandler {
    @Override
    public boolean supports(Object object) {
        return object instanceof Map;
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        Map<?, ?> map = (Map<?, ?>) object;
        if (map.isEmpty()) {
            return map;
        }
        Map<String, Object> converted = new LinkedHashMap<>();
        for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
            String key = Objects.toString(mapEntry.getKey());
            converted.put(key, ctx.convertChild(mapEntry.getValue(), key));
        }
        return converted;
    }
}
