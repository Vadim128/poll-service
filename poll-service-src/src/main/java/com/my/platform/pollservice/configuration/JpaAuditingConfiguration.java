package com.my.platform.pollservice.configuration;

import com.my.platform.pollservice.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@Configuration
@RequiredArgsConstructor
public class JpaAuditingConfiguration {

    private final SecurityUtil securityUtil;

    @Bean
    public AuditorAware<String> securityAuditor() {
        return () -> Optional.ofNullable(securityUtil.getCurrentUserId().toString());
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
