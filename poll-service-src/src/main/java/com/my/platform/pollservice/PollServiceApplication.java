package com.my.platform.pollservice;

import com.my.platform.pollservice.properties.LoggingProperties;
import com.my.platform.pollservice.properties.TechUserAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@EnableFeignClients
@EnableConfigurationProperties({
        LoggingProperties.class,
        TechUserAuthProperties.class
})
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
@SpringBootApplication(scanBasePackages = {
        "com.my.platform",
        "com.my.platform.pollservice",
})
public class PollServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PollServiceApplication.class, args);
    }

}
