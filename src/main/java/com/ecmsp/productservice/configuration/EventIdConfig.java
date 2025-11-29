package com.ecmsp.productservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class EventIdConfig {

    @Bean("eventIdSupplier")
    public Supplier<UUID> eventIdSupplier() {
        return UUID::randomUUID;
    }
}
