package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.domain.*;
import com.ecmsp.productservice.repository.CategoryRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestEntitiesGenerator {

    public static Category randomCategory() {
        return Category
                .builder()
                .id(UUID.randomUUID())
                .parentCategory(null)
                .name(TestDataGenerator.randomName())
                .build();
    }

    public static Variant randomVariant() {
        return Variant
                .builder()
                .id(UUID.randomUUID())
                .description(TestDataGenerator.randomDescription())
                .sku(TestDataGenerator.randomString(5))
                .product(randomProduct())
                .price(TestDataGenerator.randomPrice())
                .imageUrl(TestDataGenerator.randomString(5))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Product randomProduct() {
        return Product
                .builder()
                .id(UUID.randomUUID())
                .category(randomCategory())
                .name(TestDataGenerator.randomName())
                .approximatePrice(TestDataGenerator.randomPrice())
                .deliveryPrice(TestDataGenerator.randomPrice())
                .description(TestDataGenerator.randomDescription())
                .info(TestDataGenerator.randomInfo())
                .build();
    }

    public static Attribute randomAttribute() {
        return Attribute
                .builder()
                .id(UUID.randomUUID())
                .category(randomCategory())
                .name(TestDataGenerator.randomString(5))
                .unit(TestDataGenerator.randomString(2))
                .dataType(AttributeDataType.TEXT) // TODO: make it universal
                .filterable(false)
                .build();
    }

    public static AttributeValue randomAttributeValue() {
        return AttributeValue
                .builder()
                .id(UUID.randomUUID())
                .attribute(randomAttribute())
                .attributeValue(TestDataGenerator.randomString(5))
                .build();
    }

    public static VariantAttribute randomVariantAttribute() {
        return VariantAttribute
                .builder()
                .id(UUID.randomUUID())
                .variant(randomVariant())
                .attribute(randomAttribute())
                .attributeValue(randomAttributeValue())
                .valueText(TestDataGenerator.randomString(5)) // TODO: make it universal
                .build();

    }
}
