package com.zevin.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class SerializableObjectFactory {
    private Function<String, Object> fallback;
    
    public Object toSerializableObject(Object object) {
        if (object == null) {
            return null;
        }
        
        Class<?> type = object.getClass();
        
        // simple type
        if (isSimpleValueType(type)) {
            return object;
        }
        // array type
        else if (type.isArray()) {
            // empty array type
            if (Array.getLength(object) == 0) {
                return object;
            }
            
            var componentType = type.componentType();
            
            // simple array type
            if (isSimpleValueType(componentType)) {
                return object;
            }
            
            // object array type
            var length         = Array.getLength(object);
            var serializeArray = new Object[length];
            for (var i = 0; i < length; i++) {
                serializeArray[i] = toSerializableObject(Array.get(object, i));
            }
            return serializeArray;
            //            return Arrays.stream((Object[]) object).map(this::toSerializableObject).toArray();
        }
        // collection type
        else if (object instanceof final Collection<?> collection) {
            // empty collection type
            if (collection.isEmpty()) {
                return collection;
            }
            
            // simple collection type
            var optionalElementType = collection
                    .stream()
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(Object::getClass);
            if (optionalElementType.isPresent()) {
                var elementType = optionalElementType.get();
                if (ClassUtils.isSimpleValueType(elementType)) {
                    return collection;
                }
            }
            
            // object collection type
            return collection.stream().map(this::toSerializableObject).toList();
        }
        // map type
        if (object instanceof Map<?, ?> map) {
            // empty map type
            if (map.isEmpty()) {
                return map;
            }
            
            var properties = new LinkedHashMap<>();
            for (final var entry : map.entrySet()) {
                var propertyName  = Objects.toString(entry.getKey());
                var propertyValue = entry.getValue();
                properties.put(propertyName, toSerializableObject(propertyValue));
            }
            return properties;
        }
        // object type
        else {
            var properties = new LinkedHashMap<String, Object>();
            var bw  = PropertyAccessorFactory.forBeanPropertyAccess(object);
            var pds = bw.getPropertyDescriptors();
            
            for (final var pd : pds) {
                var propertyName  = pd.getDisplayName();
                var propertyValue = getPropertyValue(bw, pd);
                properties.put(propertyName, propertyValue);
            }
            return properties;
        }
    }
    
    private boolean isSimpleValueType(Class<?> type) {
        return ClassUtils.isSimpleValueType(type);
    }
    
    private Object getPropertyValue(BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor) {
        try {
            var propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
            return toSerializableObject(propertyValue);
        }
        catch (Exception e) {
            fallback.apply("");
            return "ERROR: {%s}.".formatted(e.getMessage());
        }
    }
}
