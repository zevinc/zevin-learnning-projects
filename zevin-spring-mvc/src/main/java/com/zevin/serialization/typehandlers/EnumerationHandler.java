package com.zevin.serialization.typehandlers;

import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Handles {@link Enumeration} (e.g. {@code HttpServletRequest.getHeaderNames()}).
 * Always converts to a List — Enumeration has no peek to check element types.
 */
public class EnumerationHandler implements TypeHandler {
    @Override
    public boolean supports(Object object) {
        return object instanceof Enumeration;
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        Enumeration<?> enumeration = (Enumeration<?>) object;
        if (!enumeration.hasMoreElements()) {
            return List.of();
        }
        List<Object> converted = new ArrayList<>();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            converted.add(ctx.convertChild(enumeration.nextElement(), String.valueOf(i)));
            i++;
        }
        return converted;
    }
}
