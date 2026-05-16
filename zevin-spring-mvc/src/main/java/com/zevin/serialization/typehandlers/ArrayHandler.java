package com.zevin.serialization.typehandlers;

import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Array;

/** Converts object arrays element-by-element; simple-component arrays pass through. */
public class ArrayHandler implements TypeHandler {
    @Override
    public boolean supports(Object object) {
        return object.getClass().isArray();
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        int length = Array.getLength(object);
        if (length == 0) {
            return object;
        }
        if (ClassUtils.isSimpleValueType(object.getClass().componentType())) {
            return object;
        }
        Object[] converted = new Object[length];
        for (int i = 0; i < length; i++) {
            converted[i] = ctx.convertChild(Array.get(object, i), String.valueOf(i));
        }
        return converted;
    }
}
