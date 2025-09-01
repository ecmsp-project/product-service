package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.domain.VariantAttribute;
import com.ecmsp.productservice.dto.ProductRequestDTO;
import com.ecmsp.productservice.dto.VariantAttributeRequestDTO;

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

    public static VariantAttributeRequestDTO randomVariantAttributeRequestDTO() {
        return VariantAttributeRequestDTO.builder()
                .id(TestDataGenerator.randomUUID())
                .variantId(TestDataGenerator.randomUUID())
                .attributeId(TestDataGenerator.randomUUID())
                .valueText(TestDataGenerator.randomString(5))
                .build();
    }
}
