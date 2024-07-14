package com.example.spring_cloud_gateway.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Oauth2 {
    private String protectedResourceUri;
    private String clientId;
    private String username;
    private String password;
    private String responseType;
    private String scope;
    private String redirectUri;

}
