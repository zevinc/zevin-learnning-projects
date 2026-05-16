package com.zevin.serialization;

import java.util.List;

/**
 * Formats the ancestor chain of a circular reference into a string.
 * <p>
 * Use the predefined constants or supply a lambda for custom formats.
 */
@FunctionalInterface
public interface ReferenceHandler {

    Object handle(List<Entry> chain);

    /** RFC 6901 JSON Pointer: {@code /users/0/name} */
    ReferenceHandler JSON_POINTER = chain -> {
        StringBuilder sb = new StringBuilder();
        for (Entry e : chain) {
            sb.append("/").append(e.segment());
        }
        return sb.toString();
    };

    /** JSON Reference object: {@code {"$ref": "/users/0/name"}} */
    ReferenceHandler JSON_REFERENCE = chain ->
            "{\"$ref\": \"" + JSON_POINTER.handle(chain) + "\"}";

    /**
     * JSON Path: {@code $.users[0].name}.
     * Numeric segments → {@code [N]}; named segments → {@code .name}.
     */
    ReferenceHandler JSON_PATH = chain -> {
        if (chain.isEmpty()) return "$";
        StringBuilder sb = new StringBuilder("$");
        for (Entry e : chain) {
            String seg = e.segment();
            if (seg.isEmpty()) continue;
            if (seg.matches("\\d+")) {
                sb.append("[").append(seg).append("]");
            } else {
                sb.append(".").append(seg);
            }
        }
        return sb.toString();
    };
}
