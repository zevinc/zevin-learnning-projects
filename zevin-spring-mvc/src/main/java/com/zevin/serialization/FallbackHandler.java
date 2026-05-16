package com.zevin.serialization;

import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;

/**
 * Invoked when a bean property cannot be read.
 * Receives full context for logging, exception translation, or placeholder values.
 */
public interface FallbackHandler {

    Object getPropertyValue(BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor, Exception exception);

    FallbackHandler THROW_EXCEPTION = (bw, pd, e) -> {
        throw new SerializationException("Failed to read property '%s' on %s"
                .formatted(pd.getName(), bw.getWrappedClass().getName()), e);
    };
    FallbackHandler OUTPUT_NULL = (bw, pd, e) -> null;

    FallbackHandler OUTPUT_EXCEPTION_MESSAGE = (bw, pd, e) -> "%s: %s ".formatted(e.getClass(), e.getMessage());
}
