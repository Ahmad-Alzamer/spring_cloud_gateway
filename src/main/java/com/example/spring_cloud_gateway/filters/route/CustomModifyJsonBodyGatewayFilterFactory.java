package com.example.spring_cloud_gateway.filters.route;

import com.example.spring_cloud_gateway.services.RewriteApplicationJson;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomModifyJsonBodyGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomModifyJsonBodyGatewayFilterFactory.Config> {
    //https://stackoverflow.com/a/77561362/8735058
    private final ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory;
    private final RewriteApplicationJson rewriteApplicationJson;
    public CustomModifyJsonBodyGatewayFilterFactory(ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGatewayFilterFactory, RewriteApplicationJson rewriteApplicationJson) {
        super(CustomModifyJsonBodyGatewayFilterFactory.Config.class);
        this.modifyRequestBodyGatewayFilterFactory = modifyRequestBodyGatewayFilterFactory;
        this.rewriteApplicationJson = rewriteApplicationJson;
    }

    @Override
    public GatewayFilter apply(Config config) {
        var modifyFilterConfig = new ModifyRequestBodyGatewayFilterFactory.Config()
                .setRewriteFunction(rewriteApplicationJson)
                .setInClass(String.class)
                .setOutClass(String.class);
        return modifyRequestBodyGatewayFilterFactory.apply(modifyFilterConfig);
    }

    public static class Config{}
}
