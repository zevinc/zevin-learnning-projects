package com.zevin.serialization.typehandlers;

import com.zevin.serialization.HandlerContext;
import com.zevin.serialization.TypeHandler;
import org.springframework.util.ClassUtils;

/** Returns the object unchanged when it is a simple value type. */
public class SimpleTypeHandler implements TypeHandler {
    @Override
    public boolean supports(Object object) {
        return ClassUtils.isSimpleValueType(object.getClass());
    }

    @Override
    public Object handle(Object object, HandlerContext ctx) {
        return object;
    }
}
