package com.zevin.springmvc.web;

import com.zevin.serialization.ReferenceHandler;
import com.zevin.serialization.SerializationConverter;
import com.zevin.serialization.FallbackHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class DebugController  {
    
    /**
     * 统计请求信息
     * {@link HttpServletRequest}
     */
    @GetMapping("/")
    public Object index(HttpServletRequest request) {
        return new SerializationConverter().toSerializableObject(request);
    }
}
