package com.example.spring_cloud_gateway.services;

import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;

@Component
public class RewriteMultipartFormFunction implements RewriteFunction {
    @Override
    public Object apply(Object o, Object o2) {
        return null;
    }
}
