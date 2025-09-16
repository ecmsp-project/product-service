package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pre-built test scenarios for common business cases.
 * These methods create realistic entity combinations for integration testing.
 */
public class TestScenarios {

    /**
     * Creates a complete e-commerce scenario with electronics category,
     * smartphone product, and multiple variants with properties.
     */
    public static ElectronicsScenario createElectronicsScenario() {
        return new ElectronicsScenario();
    }

    /**
     * Creates a clothing scenario with size and color variants.
     */
    public static ClothingScenario createClothingScenario() {
        return new ClothingScenario();
    }

    public static class ElectronicsScenario {
        public final Category electronicsCategory;
        public final Category smartphonesCategory;
        public final Product iPhone;
        public final Variant iPhone128GB;
        public final Variant iPhone256GB;
        public final Property storageProperty;
        public final Property colorProperty;
        public final PropertyOption storage128GB;
        public final PropertyOption storage256GB;
        public final PropertyOption colorBlack;
        public final PropertyOption colorWhite;
        public final VariantProperty iPhone128GBStorage;
        public final VariantProperty iPhone128GBColor;
        public final VariantProperty iPhone256GBStorage;
        public final VariantProperty iPhone256GBColor;

        private ElectronicsScenario() {
            // Categories
            electronicsCategory = TestEntityBuilder.category()
                    .withName("Electronics")
                    .build();

            smartphonesCategory = TestEntityBuilder.category()
                    .withName("Smartphones")
                    .withParent(electronicsCategory)
                    .build();

            // Product
            iPhone = TestEntityBuilder.product()
                    .withName("iPhone 15")
                    .withCategory(smartphonesCategory)
                    .withPrices(new BigDecimal("999.99"), new BigDecimal("29.99"))
                    .withDescription("Latest iPhone with advanced features")
                    .build();

            // Properties
            storageProperty = TestEntityBuilder.property()
                    .withName("Storage")
                    .withCategory(smartphonesCategory)
                    .asTextProperty()
                    .withUnit("GB")
                    .filterable()
                    .required()
                    .build();

            colorProperty = TestEntityBuilder.property()
                    .withName("Color")
                    .withCategory(smartphonesCategory)
                    .asTextProperty()
                    .filterable()
                    .required()
                    .build();

            // Property Options
            storage128GB = TestEntityBuilder.propertyOption()
                    .withProperty(storageProperty)
                    .withTextValue("128GB")
                    .withDisplayText("128 GB Storage")
                    .build();

            storage256GB = TestEntityBuilder.propertyOption()
                    .withProperty(storageProperty)
                    .withTextValue("256GB")
                    .withDisplayText("256 GB Storage")
                    .build();

            colorBlack = TestEntityBuilder.propertyOption()
                    .withProperty(colorProperty)
                    .withTextValue("Black")
                    .withDisplayText("Space Black")
                    .build();

            colorWhite = TestEntityBuilder.propertyOption()
                    .withProperty(colorProperty)
                    .withTextValue("White")
                    .withDisplayText("Arctic White")
                    .build();

            // Variants
            iPhone128GB = TestEntityBuilder.variant()
                    .withProduct(iPhone)
                    .withSku("IP15-128-BLK")
                    .withPrice(new BigDecimal("999.99"))
                    .withStock(50)
                    .withDescription("iPhone 15 128GB Black")
                    .build();

            iPhone256GB = TestEntityBuilder.variant()
                    .withProduct(iPhone)
                    .withSku("IP15-256-WHT")
                    .withPrice(new BigDecimal("1199.99"))
                    .withStock(25)
                    .withDescription("iPhone 15 256GB White")
                    .build();

            // Variant Properties
            iPhone128GBStorage = TestEntityBuilder.variantProperty()
                    .withVariant(iPhone128GB)
                    .withProperty(storageProperty)
                    .withPropertyOption(storage128GB)
                    .build();

            iPhone128GBColor = TestEntityBuilder.variantProperty()
                    .withVariant(iPhone128GB)
                    .withProperty(colorProperty)
                    .withPropertyOption(colorBlack)
                    .build();

            iPhone256GBStorage = TestEntityBuilder.variantProperty()
                    .withVariant(iPhone256GB)
                    .withProperty(storageProperty)
                    .withPropertyOption(storage256GB)
                    .build();

            iPhone256GBColor = TestEntityBuilder.variantProperty()
                    .withVariant(iPhone256GB)
                    .withProperty(colorProperty)
                    .withPropertyOption(colorWhite)
                    .build();
        }
    }

    public static class ClothingScenario {
        public final Category clothingCategory;
        public final Category shirtsCategory;
        public final Product tShirt;
        public final Variant tShirtSmallRed;
        public final Variant tShirtLargeBlue;
        public final Property sizeProperty;
        public final Property colorProperty;
        public final PropertyOption sizeSmall;
        public final PropertyOption sizeLarge;
        public final VariantProperty tShirtSmallSize;
        public final VariantProperty tShirtSmallColor;
        public final VariantProperty tShirtLargeSize;
        public final VariantProperty tShirtLargeColor;

        private ClothingScenario() {
            // Categories
            clothingCategory = TestEntityBuilder.category()
                    .withName("Clothing")
                    .build();

            shirtsCategory = TestEntityBuilder.category()
                    .withName("T-Shirts")
                    .withParent(clothingCategory)
                    .build();

            // Product
            tShirt = TestEntityBuilder.product()
                    .withName("Cotton T-Shirt")
                    .withCategory(shirtsCategory)
                    .withPrices(new BigDecimal("29.99"), new BigDecimal("5.99"))
                    .withDescription("Comfortable cotton t-shirt")
                    .build();

            // Properties
            sizeProperty = TestEntityBuilder.property()
                    .withName("Size")
                    .withCategory(shirtsCategory)
                    .asTextProperty()
                    .filterable()
                    .required()
                    .build();

            colorProperty = TestEntityBuilder.property()
                    .withName("Color")
                    .withCategory(shirtsCategory)
                    .asTextProperty()
                    .filterable()
                    .required()
                    .build();

            // Property Options
            sizeSmall = TestEntityBuilder.propertyOption()
                    .withProperty(sizeProperty)
                    .withTextValue("S")
                    .withDisplayText("Small")
                    .build();

            sizeLarge = TestEntityBuilder.propertyOption()
                    .withProperty(sizeProperty)
                    .withTextValue("L")
                    .withDisplayText("Large")
                    .build();

            // Variants
            tShirtSmallRed = TestEntityBuilder.variant()
                    .withProduct(tShirt)
                    .withSku("TSH-S-RED")
                    .withPrice(new BigDecimal("29.99"))
                    .withStock(30)
                    .build();

            tShirtLargeBlue = TestEntityBuilder.variant()
                    .withProduct(tShirt)
                    .withSku("TSH-L-BLU")
                    .withPrice(new BigDecimal("29.99"))
                    .withStock(15)
                    .build();

            // Variant Properties with custom values (no predefined options)
            tShirtSmallSize = TestEntityBuilder.variantProperty()
                    .withVariant(tShirtSmallRed)
                    .withProperty(sizeProperty)
                    .withPropertyOption(sizeSmall)
                    .build();

            tShirtSmallColor = TestEntityBuilder.variantProperty()
                    .withVariant(tShirtSmallRed)
                    .forColor("Red")
                    .build();

            tShirtLargeSize = TestEntityBuilder.variantProperty()
                    .withVariant(tShirtLargeBlue)
                    .withProperty(sizeProperty)
                    .withPropertyOption(sizeLarge)
                    .build();

            tShirtLargeColor = TestEntityBuilder.variantProperty()
                    .withVariant(tShirtLargeBlue)
                    .forColor("Blue")
                    .build();
        }
    }

    /**
     * Creates a simple scenario for testing basic functionality.
     */
    public static SimpleProductScenario createSimpleProductScenario() {
        return new SimpleProductScenario();
    }

    public static class SimpleProductScenario {
        public final Category category;
        public final Product product;
        public final Variant variant;

        private SimpleProductScenario() {
            category = TestEntityBuilder.category()
                    .withName("Test Category")
                    .build();

            product = TestEntityBuilder.product()
                    .withName("Test Product")
                    .withCategory(category)
                    .build();

            variant = TestEntityBuilder.variant()
                    .withProduct(product)
                    .withSku("TEST-001")
                    .inStock()
                    .build();
        }
    }

    /**
     * Creates scenario with out of stock variants for testing inventory logic.
     */
    public static OutOfStockScenario createOutOfStockScenario() {
        return new OutOfStockScenario();
    }

    public static class OutOfStockScenario {
        public final Category category;
        public final Product product;
        public final Variant outOfStockVariant;
        public final Variant lowStockVariant;

        private OutOfStockScenario() {
            category = TestEntityBuilder.category()
                    .withName("Limited Stock Category")
                    .build();

            product = TestEntityBuilder.product()
                    .withName("Limited Edition Product")
                    .withCategory(category)
                    .build();

            outOfStockVariant = TestEntityBuilder.variant()
                    .withProduct(product)
                    .withSku("LIMITED-001")
                    .outOfStock()
                    .build();

            lowStockVariant = TestEntityBuilder.variant()
                    .withProduct(product)
                    .withSku("LIMITED-002")
                    .withStock(2)
                    .build();
        }
    }
}