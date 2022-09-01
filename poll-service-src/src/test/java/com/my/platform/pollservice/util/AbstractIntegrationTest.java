package com.my.platform.pollservice.util;

import com.my.platform.pollservice.initializer.WiremockServerInitializer;
import com.my.platform.pollservice.initializer.PostgresInitializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
        initializers = {
                PostgresInitializer.class,
                WiremockServerInitializer.class
        }
)
@ActiveProfiles("test")
@SpringBootTest
public class AbstractIntegrationTest {

}
