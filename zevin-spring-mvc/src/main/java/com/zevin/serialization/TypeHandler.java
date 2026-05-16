package com.zevin.serialization;

/**
 * Pluggable strategy for handling a category of objects.
 * <p>
 * Handlers are consulted in insertion order; the first handler whose
 * {@link #supports} returns {@code true} is used.
 */
public interface TypeHandler {

    boolean supports(Object object);

    /**
     * Convert {@code object} to a serializable form.  Use
     * {@link HandlerContext#convertChild} to recursively convert
     * nested objects.
     */
    Object handle(Object object, HandlerContext ctx);
}
