package com.zevin.serialization.typehandlers;

import com.zevin.serialization.FallbackHandler;
import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catch-all handler — must be the <b>last</b> handler in the chain.
 * Reflects over bean properties and recursively converts each value.
 */
public class BeanHandler implements TypeHandler {
    private final FallbackHandler fallbackHandler;

    public BeanHandler(FallbackHandler fallbackHandler) {
        this.fallbackHandler = fallbackHandler;
    }

    @Override
    public boolean supports(Object object) {
        return true;
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        Map<String, Object> converted = new LinkedHashMap<>();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(object);
        for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
            String name = pd.getDisplayName();
            converted.put(name, readProperty(bw, pd, ctx));
        }
        return converted;
    }

    private Object readProperty(BeanWrapper bw, PropertyDescriptor pd, HandlerContext ctx) {
        try {
            return ctx.convertChild(bw.getPropertyValue(pd.getName()), pd.getName());
        } catch (Exception e) {
            return fallbackHandler.getPropertyValue(bw, pd, e);
        }
    }
}
