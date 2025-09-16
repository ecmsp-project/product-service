package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Comprehensive test entity builder with fluent API and realistic defaults.
 * Provides methods to create entities with relationships and custom values.
 */
public class TestEntityBuilder {

    // Category Builder
    public static CategoryBuilder category() {
        return new CategoryBuilder();
    }

    public static class CategoryBuilder {
        private UUID id = UUID.randomUUID();
        private String name = TestDataGenerator.randomName();
        private Category parentCategory = null;

        public CategoryBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder withParent(Category parent) {
            this.parentCategory = parent;
            return this;
        }

        public CategoryBuilder asChild() {
            this.parentCategory = category().build();
            return this;
        }

        public Category build() {
            return Category.builder()
                    .id(id)
                    .name(name)
                    .parentCategory(parentCategory)
                    .build();
        }
    }

    // Product Builder
    public static ProductBuilder product() {
        return new ProductBuilder();
    }

    public static class ProductBuilder {
        private UUID id = UUID.randomUUID();
        private Category category = TestEntityBuilder.category().build();
        private String name = TestDataGenerator.randomName();
        private BigDecimal approximatePrice = TestDataGenerator.randomPrice();
        private BigDecimal deliveryPrice = TestDataGenerator.randomPrice();
        private String description = TestDataGenerator.randomDescription();

        public ProductBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public ProductBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withPrice(BigDecimal price) {
            this.approximatePrice = price;
            return this;
        }

        public ProductBuilder withPrices(BigDecimal approximatePrice, BigDecimal deliveryPrice) {
            this.approximatePrice = approximatePrice;
            this.deliveryPrice = deliveryPrice;
            return this;
        }

        public ProductBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder inCategory(String categoryName) {
            this.category = category().withName(categoryName).build();
            return this;
        }

        public Product build() {
            return Product.builder()
                    .id(id)
                    .category(category)
                    .name(name)
                    .approximatePrice(approximatePrice)
                    .deliveryPrice(deliveryPrice)
                    .description(description)
                    .info(TestDataGenerator.randomInfo())
                    .build();
        }
    }

    // Variant Builder
    public static VariantBuilder variant() {
        return new VariantBuilder();
    }

    public static class VariantBuilder {
        private UUID id = UUID.randomUUID();
        private Product product = TestEntityBuilder.product().build();
        private String sku = TestDataGenerator.randomString(10);
        private BigDecimal price = TestDataGenerator.randomPrice();
        private int stockQuantity = TestDataGenerator.randomInt(0, 100);
        private String imageUrl = "https://example.com/image.jpg";
        private String description = TestDataGenerator.randomDescription();
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public VariantBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public VariantBuilder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public VariantBuilder withSku(String sku) {
            this.sku = sku;
            return this;
        }

        public VariantBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public VariantBuilder withStock(int quantity) {
            this.stockQuantity = quantity;
            return this;
        }

        public VariantBuilder outOfStock() {
            this.stockQuantity = 0;
            return this;
        }

        public VariantBuilder inStock() {
            this.stockQuantity = TestDataGenerator.randomInt(1, 50);
            return this;
        }

        public VariantBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public VariantBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Variant build() {
            return Variant.builder()
                    .id(id)
                    .product(product)
                    .sku(sku)
                    .price(price)
                    .stockQuantity(stockQuantity)
                    .imageUrl(imageUrl)
                    .description(description)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }
    }

    // Property Builder
    public static PropertyBuilder property() {
        return new PropertyBuilder();
    }

    public static class PropertyBuilder {
        private UUID id = UUID.randomUUID();
        private Category category = TestEntityBuilder.category().build();
        private String name = "Property_" + TestDataGenerator.randomString(5);
        private String unit = null;
        private AttributeDataType dataType = AttributeDataType.TEXT;
        private boolean filterable = true;
        private boolean required = false;

        public PropertyBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public PropertyBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public PropertyBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PropertyBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public PropertyBuilder withDataType(AttributeDataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public PropertyBuilder filterable() {
            this.filterable = true;
            return this;
        }

        public PropertyBuilder notFilterable() {
            this.filterable = false;
            return this;
        }

        public PropertyBuilder required() {
            this.required = true;
            return this;
        }

        public PropertyBuilder optional() {
            this.required = false;
            return this;
        }

        public PropertyBuilder asTextProperty() {
            this.dataType = AttributeDataType.TEXT;
            return this;
        }

        public PropertyBuilder asNumberProperty() {
            this.dataType = AttributeDataType.NUMBER;
            this.unit = "pcs";
            return this;
        }

        public PropertyBuilder asBooleanProperty() {
            this.dataType = AttributeDataType.BOOLEAN;
            return this;
        }

        public PropertyBuilder asDateProperty() {
            this.dataType = AttributeDataType.DATE;
            return this;
        }

        public Property build() {
            return Property.builder()
                    .id(id)
                    .category(category)
                    .name(name)
                    .unit(unit)
                    .dataType(dataType)
                    .filterable(filterable)
                    .required(required)
                    .build();
        }
    }

    // PropertyOption Builder
    public static PropertyOptionBuilder propertyOption() {
        return new PropertyOptionBuilder();
    }

    public static class PropertyOptionBuilder {
        private UUID id = UUID.randomUUID();
        private Property property = TestEntityBuilder.property().build();
        private String valueText = null;
        private BigDecimal valueDecimal = null;
        private Boolean valueBoolean = null;
        private LocalDate valueDate = null;
        private String displayText = "Default Display Text";

        public PropertyOptionBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public PropertyOptionBuilder withProperty(Property property) {
            this.property = property;
            return this;
        }

        public PropertyOptionBuilder withTextValue(String value) {
            this.valueText = value;
            this.displayText = value;
            return this;
        }

        public PropertyOptionBuilder withDecimalValue(BigDecimal value) {
            this.valueDecimal = value;
            this.displayText = value.toString();
            return this;
        }

        public PropertyOptionBuilder withBooleanValue(Boolean value) {
            this.valueBoolean = value;
            this.displayText = value.toString();
            return this;
        }

        public PropertyOptionBuilder withDateValue(LocalDate value) {
            this.valueDate = value;
            this.displayText = value.toString();
            return this;
        }

        public PropertyOptionBuilder withDisplayText(String displayText) {
            this.displayText = displayText;
            return this;
        }

        public PropertyOption build() {
            return PropertyOption.builder()
                    .id(id)
                    .property(property)
                    .valueText(valueText)
                    .valueDecimal(valueDecimal)
                    .valueBoolean(valueBoolean)
                    .valueDate(valueDate)
                    .displayText(displayText)
                    .build();
        }
    }

    // VariantProperty Builder
    public static VariantPropertyBuilder variantProperty() {
        return new VariantPropertyBuilder();
    }

    public static class VariantPropertyBuilder {
        private UUID id = UUID.randomUUID();
        private Variant variant = TestEntityBuilder.variant().build();
        private Property property = TestEntityBuilder.property().build();
        private PropertyOption propertyOption = null;
        private String customValueText = null;
        private BigDecimal customValueDecimal = null;
        private Boolean customValueBoolean = null;
        private LocalDate customValueDate = null;

        public VariantPropertyBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public VariantPropertyBuilder withVariant(Variant variant) {
            this.variant = variant;
            return this;
        }

        public VariantPropertyBuilder withProperty(Property property) {
            this.property = property;
            return this;
        }

        public VariantPropertyBuilder withPropertyOption(PropertyOption propertyOption) {
            this.propertyOption = propertyOption;
            return this;
        }

        public VariantPropertyBuilder withCustomTextValue(String value) {
            this.customValueText = value;
            return this;
        }

        public VariantPropertyBuilder withCustomDecimalValue(BigDecimal value) {
            this.customValueDecimal = value;
            return this;
        }

        public VariantPropertyBuilder withCustomBooleanValue(Boolean value) {
            this.customValueBoolean = value;
            return this;
        }

        public VariantPropertyBuilder withCustomDateValue(LocalDate value) {
            this.customValueDate = value;
            return this;
        }

        public VariantPropertyBuilder forColor(String color) {
            this.property = property().withName("Color").asTextProperty().build();
            this.customValueText = color;
            return this;
        }

        public VariantPropertyBuilder forSize(String size) {
            this.property = property().withName("Size").asTextProperty().build();
            this.customValueText = size;
            return this;
        }

        public VariantPropertyBuilder forWeight(BigDecimal weight) {
            this.property = property().withName("Weight").asNumberProperty().withUnit("kg").build();
            this.customValueDecimal = weight;
            return this;
        }

        public VariantProperty build() {
            return VariantProperty.builder()
                    .id(id)
                    .variant(variant)
                    .property(property)
                    .propertyOption(propertyOption)
                    .customValueText(customValueText)
                    .customValueDecimal(customValueDecimal)
                    .customValueBoolean(customValueBoolean)
                    .customValueDate(customValueDate)
                    .build();
        }
    }
}