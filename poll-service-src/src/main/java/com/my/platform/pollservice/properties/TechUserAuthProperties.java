package com.my.platform.pollservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = TechUserAuthProperties.PREFIX)
public class TechUserAuthProperties {
    public static final String PREFIX = "tech.user.auth";

    private String clientId;
    private String username;
    private String password;
}
