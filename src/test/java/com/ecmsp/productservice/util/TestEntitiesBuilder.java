package com.ecmsp.productservice.util;

import com.ecmsp.productservice.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TestEntitiesBuilder {

    public static Category.CategoryBuilder categoryBuilder() {
        return Category.builder()
                .name("Test Category");
    }

    public static Product.ProductBuilder productBuilder() {
        return Product.builder()
                .name("Test Product")
                .approximatePrice(new BigDecimal("99.99"))
                .deliveryPrice(new BigDecimal("9.99"))
                .description("Test product description")
                .info(new HashMap<>());
    }

    public static Property.PropertyBuilder propertyBuilder() {
        return Property.builder()
                .name("Test Property")
                .unit("kg")
                .dataType(PropertyDataType.TEXT)
                .required(true)
                .hasDefaultOptions(false);
    }

    public static Variant.VariantBuilder variantBuilder() {
        return Variant.builder()
                .price(new BigDecimal("89.99"))
                .stockQuantity(10)
                .imageUrl("http://example.com/image.jpg")
                .description("Test variant description")
                .additionalProperties(new HashMap<>());
    }

    public static DefaultPropertyOption.DefaultPropertyOptionBuilder defaultPropertyOptionBuilder() {
        return DefaultPropertyOption.builder()
                .displayText("Test Option")
                .valueText("Test Value");
    }

    public static VariantProperty.VariantPropertyBuilder variantPropertyBuilder() {
        return VariantProperty.builder()
                .displayText("Test Display Text")
                .valueText("Test Value");
    }

    // Specific builder methods for different property data types
    public static DefaultPropertyOption.DefaultPropertyOptionBuilder textPropertyOptionBuilder() {
        return DefaultPropertyOption.builder()
                .displayText("Text Option")
                .valueText("Sample Text");
    }

    public static DefaultPropertyOption.DefaultPropertyOptionBuilder numberPropertyOptionBuilder() {
        return DefaultPropertyOption.builder()
                .displayText("Number Option")
                .valueDecimal(new BigDecimal("42.5"));
    }

    public static DefaultPropertyOption.DefaultPropertyOptionBuilder booleanPropertyOptionBuilder() {
        return DefaultPropertyOption.builder()
                .displayText("Boolean Option")
                .valueBoolean(true);
    }

    public static DefaultPropertyOption.DefaultPropertyOptionBuilder datePropertyOptionBuilder() {
        return DefaultPropertyOption.builder()
                .displayText("Date Option")
                .valueDate(LocalDate.of(2024, 1, 1));
    }

    // Specific builder methods for variant properties
    public static VariantProperty.VariantPropertyBuilder textVariantPropertyBuilder() {
        return VariantProperty.builder()
                .displayText("Text Property")
                .valueText("Sample Text");
    }

    public static VariantProperty.VariantPropertyBuilder numberVariantPropertyBuilder() {
        return VariantProperty.builder()
                .displayText("Number Property")
                .valueDecimal(new BigDecimal("123.45"));
    }

    public static VariantProperty.VariantPropertyBuilder booleanVariantPropertyBuilder() {
        return VariantProperty.builder()
                .displayText("Boolean Property")
                .valueBoolean(false);
    }

    public static VariantProperty.VariantPropertyBuilder dateVariantPropertyBuilder() {
        return VariantProperty.builder()
                .displayText("Date Property")
                .valueDate(LocalDate.of(2024, 12, 31));
    }

    // Complete entity builders with relationships
    public static Category buildCategory() {
        return categoryBuilder().build();
    }

    public static Category buildCategoryWithParent(Category parent) {
        return categoryBuilder()
                .parentCategory(parent)
                .build();
    }

    public static Product buildProduct(Category category) {
        return productBuilder()
                .category(category)
                .build();
    }

    public static Property buildProperty(Category category) {
        return propertyBuilder()
                .category(category)
                .build();
    }

    public static Property buildProperty(Category category, PropertyDataType dataType) {
        return propertyBuilder()
                .category(category)
                .dataType(dataType)
                .build();
    }

    public static Variant buildVariant(Product product) {
        return variantBuilder()
                .product(product)
                .build();
    }

    public static DefaultPropertyOption buildDefaultPropertyOption(Property property) {
        return switch (property.getDataType()) {
            case TEXT -> textPropertyOptionBuilder().property(property).build();
            case NUMBER -> numberPropertyOptionBuilder().property(property).build();
            case BOOLEAN -> booleanPropertyOptionBuilder().property(property).build();
            case DATE -> datePropertyOptionBuilder().property(property).build();
        };
    }

    public static VariantProperty buildVariantProperty(Variant variant, Property property) {
        return switch (property.getDataType()) {
            case TEXT -> textVariantPropertyBuilder().variant(variant).property(property).build();
            case NUMBER -> numberVariantPropertyBuilder().variant(variant).property(property).build();
            case BOOLEAN -> booleanVariantPropertyBuilder().variant(variant).property(property).build();
            case DATE -> dateVariantPropertyBuilder().variant(variant).property(property).build();
        };
    }
}