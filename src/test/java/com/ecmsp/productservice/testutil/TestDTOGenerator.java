package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.dto.ProductRequestDTO;

import java.util.UUID;

public class TestDTOGenerator {

    public static ProductRequestDTO randomProductRequestDTO() {
        return ProductRequestDTO.builder()
                .name(TestDataGenerator.randomString(5))
                .deliveryPrice(TestDataGenerator.randomPrice())
                .approximatePrice(TestDataGenerator.randomPrice())
                .description(TestDataGenerator.randomString(10))
                .info(TestDataGenerator.randomInfo())
                .categoryId(UUID.randomUUID())
                .build();
    }
}
