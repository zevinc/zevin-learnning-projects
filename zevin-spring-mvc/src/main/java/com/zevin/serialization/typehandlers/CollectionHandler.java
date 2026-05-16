package com.zevin.serialization.typehandlers;

import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * Converts collections element-by-element, preserving the original collection
 * type where possible.  Collections of simple types pass through unchanged.
 */
public class CollectionHandler implements TypeHandler {
    @Override
    public boolean supports(Object object) {
        return object instanceof Collection;
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        Collection<?> collection = (Collection<?>) object;
        if (collection.isEmpty()) {
            return collection;
        }
        if (allElementsSimple(collection)) {
            return collection;
        }
        Collection<Object> converted = newCollectionLike(collection);
        int i = 0;
        for (Object element : collection) {
            converted.add(ctx.convertChild(element, String.valueOf(i)));
            i++;
        }
        return converted;
    }

    private static boolean allElementsSimple(Collection<?> c) {
        return c.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Object::getClass)
                .filter(ClassUtils::isSimpleValueType)
                .isPresent();
    }

    @SuppressWarnings("unchecked")
    private static <T> Collection<T> newCollectionLike(Collection<?> source) {
        try {
            return (Collection<T>) source.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return source instanceof Set ? new LinkedHashSet<>() : new ArrayList<>();
        }
    }
}
