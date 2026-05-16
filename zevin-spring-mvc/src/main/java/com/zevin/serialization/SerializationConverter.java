package com.zevin.serialization;

import com.zevin.serialization.typehandlers.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recursively converts an object graph into a serializable form — primitives,
 * arrays, collections, maps, enumerations, and Java beans.
 * <p>
 * Circular references are detected via reference-identity tracking and
 * replaced by a formatted reference string.
 * <p>
 * <b>Extension points (Strategy pattern):</b>
 * <ul>
 *   <li>{@link TypeHandler}     — add custom handlers or reorder via {@link #getTypeHandlers()}</li>
 *   <li>{@link ReferenceHandler} — how circular references are rendered</li>
 *   <li>{@link FallbackHandler}        — what to do when a bean property cannot be read</li>
 * </ul>
 */
@Getter
public class SerializationConverter {
    private final ReferenceHandler referenceHandler;
    private final FallbackHandler fallbackHandler;
    private final List<TypeHandler> typeHandlers;

    public SerializationConverter() {
        this.referenceHandler = ReferenceHandler.JSON_POINTER;
        this.fallbackHandler = FallbackHandler.OUTPUT_EXCEPTION_MESSAGE;
        this.typeHandlers = defaultTypeHandlers();
    }

    public SerializationConverter(FallbackHandler fallbackHandler, ReferenceHandler referenceHandler, List<TypeHandler> typeHandlers) {
        this.fallbackHandler = fallbackHandler;
        this.referenceHandler = referenceHandler;
        this.typeHandlers = typeHandlers;
    }

    // -- public API --------------------------------------------------

    public Object toSerializableObject(Object object) {
        return new ConversionSession().convertChild(object, "");
    }

    // -- internals ---------------------------------------------------

    private List<TypeHandler> defaultTypeHandlers() {
        return List.of(new SimpleTypeHandler(),
                new ArrayHandler(),
                new CollectionHandler(),
                new MapHandler(),
                new EnumerationHandler(),
                new BeanHandler(fallbackHandler));
    }

    private Object convertInternal(ConversionSession session, Object object, String segment) {
        if (object == null) {
            return null;
        }

        List<Entry> existingChain = session.visited.get(object);
        if (existingChain != null) {
            return referenceHandler.handle(existingChain);
        }

        Entry entry = new Entry(object, segment);
        session.chain.add(entry);
        session.visited.put(object, List.copyOf(session.chain));

        try {
            for (TypeHandler handler : typeHandlers) {
                if (handler.supports(object)) {
                    return handler.handle(object, session);
                }
            }
            throw new IllegalStateException(
                    "No TypeHandler supports: " + object.getClass().getName());
        } finally {
            session.chain.remove(session.chain.size() - 1);
            session.visited.remove(object);
        }
    }

    /**
     * Mutable per-call state.  Implements {@link HandlerContext} so handlers
     * can recurse without touching the converter's internals.
     */
    private class ConversionSession implements HandlerContext {
        final Map<Object, List<Entry>> visited = new IdentityHashMap<>();
        final List<Entry> chain = new ArrayList<>();

        @Override
        public Object convertChild(Object child, String segment) {
            return convertInternal(this, child, segment);
        }
    }
}
