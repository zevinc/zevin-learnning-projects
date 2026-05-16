package com.zevin.utils;

import lombok.Getter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Recursively converts an object graph into a serializable form: primitives,
 * arrays, collections, and maps of maps.  Circular references are detected
 * via reference-identity tracking and replaced with a formatted reference
 * string (JSON Pointer, JSON Reference, JSON Path, or custom).
 */
@Getter
public class DeepSerializationConverter {
    private final Fallback fallback;
    private final ReferenceFormat referenceFormat;

    public DeepSerializationConverter(Fallback fallback) {
        this(fallback, ReferenceFormat.JSON_POINTER);
    }

    public DeepSerializationConverter(Fallback fallback, ReferenceFormat referenceFormat) {
        this.fallback = fallback;
        this.referenceFormat = referenceFormat;
    }

    // -- public API --------------------------------------------------

    public Object toSerializableObject(Object object) {
        return toSerializableObject(object, new IdentityHashMap<>(), new ArrayList<>(), "");
    }

    // -- internals ---------------------------------------------------

    /**
     * Node in the path from root to the current object.
     * {@code segment} is the JSON Pointer path segment that led here:
     * a property name, an array index, a map key, or {@code ""} for root.
     */
    public record Entry(Object object, String segment) {}

    /**
     * Recursive worker.
     *
     * @param visited  identity-based map from object → chain snapshot at first encounter.
     *                 {@link IdentityHashMap} uses {@code ==} rather than {@code equals},
     *                 so two distinct objects that happen to be equal are not treated as
     *                 the same node.
     * @param chain    mutable path from root to current node.  A {@link List#copyOf} snapshot
     *                 is stored in {@code visited} at first visit so later circular hits see
     *                 the chain as it was at the original site.
     * @param segment  JSON Pointer segment that led to {@code object} (empty for root)
     */
    private Object toSerializableObject(Object object,
                                         Map<Object, List<Entry>> visited,
                                         List<Entry> chain,
                                         String segment) {
        if (object == null) {
            return null;
        }

        // Circular-reference guard: if this object was already visited,
        // format the chain from its first occurrence and return it as a placeholder.
        List<Entry> existingChain = visited.get(object);
        if (existingChain != null) {
            return referenceFormat.format(existingChain);
        }

        Entry entry = new Entry(object, segment);
        chain.add(entry);
        visited.put(object, List.copyOf(chain));   // defensive snapshot — chain mutates below

        try {
            Class<?> type = object.getClass();

            if (isSimpleValueType(type)) {
                return object;
            }

            if (type.isArray()) {
                return convertArray(object, visited, chain);
            }

            if (object instanceof Collection<?> collection) {
                return convertCollection(collection, visited, chain);
            }

            if (object instanceof Map<?, ?> map) {
                return convertMap(map, visited, chain);
            }

            return convertBean(object, visited, chain);

        } finally {
            // Restore state so sibling branches see the correct chain and
            // visited set — the current object is done at this depth.
            chain.remove(chain.size() - 1);
            visited.remove(object);
        }
    }

    // -- type predicates ---------------------------------------------

    private boolean isSimpleValueType(Class<?> type) {
        return ClassUtils.isSimpleValueType(type);
    }

    // -- per-shape converters ----------------------------------------

    private Object convertArray(Object array,
                                 Map<Object, List<Entry>> visited,
                                 List<Entry> chain) {
        int length = Array.getLength(array);
        if (length == 0) {
            return array;
        }

        Class<?> componentType = array.getClass().componentType();
        if (isSimpleValueType(componentType)) {
            return array;
        }

        Object[] converted = new Object[length];
        for (int i = 0; i < length; i++) {
            converted[i] = toSerializableObject(Array.get(array, i), visited, chain, String.valueOf(i));
        }
        return converted;
    }

    private Object convertCollection(Collection<?> collection,
                                      Map<Object, List<Entry>> visited,
                                      List<Entry> chain) {
        if (collection.isEmpty()) {
            return collection;
        }

        if (allElementsSimple(collection)) {
            return collection;
        }

        Collection<Object> converted = newCollectionLike(collection);
        int i = 0;
        for (Object element : collection) {
            converted.add(toSerializableObject(element, visited, chain, String.valueOf(i)));
            i++;
        }
        return converted;
    }

    private boolean allElementsSimple(Collection<?> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(Object::getClass)
                .filter(ClassUtils::isSimpleValueType)
                .isPresent();
    }

    /**
     * Try to instantiate a collection of the same type as {@code source}
     * so the caller sees {@code LinkedHashSet} / {@code TreeSet} instead
     * of a generic {@code List}.  Falls back to {@link LinkedHashSet}
     * for sets, {@link ArrayList} otherwise.
     */
    @SuppressWarnings("unchecked")
    private <T> Collection<T> newCollectionLike(Collection<?> source) {
        try {
            return (Collection<T>) source.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return source instanceof Set ? new LinkedHashSet<>() : new ArrayList<>();
        }
    }

    private Object convertMap(Map<?, ?> map,
                               Map<Object, List<Entry>> visited,
                               List<Entry> chain) {
        if (map.isEmpty()) {
            return map;
        }

        Map<String, Object> converted = new LinkedHashMap<>();
        for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
            String key = Objects.toString(mapEntry.getKey());
            converted.put(key, toSerializableObject(mapEntry.getValue(), visited, chain, key));
        }
        return converted;
    }

    private Object convertBean(Object bean,
                                Map<Object, List<Entry>> visited,
                                List<Entry> chain) {
        Map<String, Object> converted = new LinkedHashMap<>();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);

        for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
            String name = pd.getDisplayName();
            converted.put(name, getPropertyValue(bw, pd, visited, chain));
        }
        return converted;
    }

    // -- property access ---------------------------------------------

    private Object getPropertyValue(BeanWrapper bw, PropertyDescriptor pd,
                                     Map<Object, List<Entry>> visited,
                                     List<Entry> chain) {
        try {
            Object value = bw.getPropertyValue(pd.getName());
            return toSerializableObject(value, visited, chain, pd.getName());
        } catch (Exception e) {
            return fallback.getPropertyValue(bw, pd, e);
        }
    }

    // -- nested types ------------------------------------------------

    /**
     * Formats the ancestor chain of a circular reference into a string.
     * <p>
     * Receives every node from root to the first occurrence of the
     * circular object.  Use the predefined constants, or supply a
     * lambda for custom formats.
     */
    @FunctionalInterface
    public interface ReferenceFormat {

        String format(List<Entry> chain);

        /** RFC 6901 JSON Pointer: {@code /users/0/name} */
        ReferenceFormat JSON_POINTER = chain -> {
            StringBuilder sb = new StringBuilder();
            for (Entry e : chain) {
                sb.append("/").append(e.segment());
            }
            return sb.toString();
        };

        /** JSON Reference object: {@code {"$ref": "/users/0/name"}} */
        ReferenceFormat JSON_REFERENCE = chain ->
                "{\"$ref\": \"" + JSON_POINTER.format(chain) + "\"}";

        /**
         * JSON Path: {@code $.users[0].name}.
         * Numeric segments become {@code [N]}; named segments become {@code .name}.
         */
        ReferenceFormat JSON_PATH = chain -> {
            if (chain.isEmpty()) {
                return "$";
            }
            StringBuilder sb = new StringBuilder("$");
            for (Entry e : chain) {
                String seg = e.segment();
                if (seg.isEmpty()) {
                    continue;                       // skip root's empty segment
                }
                if (seg.matches("\\d+")) {
                    sb.append("[").append(seg).append("]");
                } else {
                    sb.append(".").append(seg);
                }
            }
            return sb.toString();
        };
    }

    /**
     * Invoked when a bean property cannot be read.
     * Receives full context for logging, exception translation, or placeholder values.
     */
    public interface Fallback {
        Fallback THROW = (bw, pd, e) -> {
            throw new RuntimeException("Failed to read property '%s' on %s"
                    .formatted(pd.getName(), bw.getWrappedClass().getName()), e);
        };
        Fallback NULL = (bw, pd, e) -> null;

        Object getPropertyValue(BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor, Exception exception);
    }
}
