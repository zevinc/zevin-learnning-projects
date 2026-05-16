package com.zevin.serialization;

/**
 * Context passed to {@link TypeHandler#handle} — provides recursion
 * without exposing the converter's internal state.
 */
public interface HandlerContext {

    /**
     * Recursively convert a child object.
     * {@code segment} is the JSON Pointer segment for this child
     * (property name, array index, map key, or {@code ""} for root).
     */
    Object convertChild(Object child, String segment);
}
