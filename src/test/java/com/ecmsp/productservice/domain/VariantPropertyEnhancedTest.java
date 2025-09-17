package com.ecmsp.productservice.domain;

import com.ecmsp.productservice.testutil.TestEntityBuilder;
import com.ecmsp.productservice.testutil.TestScenarios;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VariantProperty Enhanced Unit Tests")
class VariantPropertyEnhancedTest {

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should create VariantProperty using TestEntityBuilder fluent API")
        void shouldCreateVariantPropertyUsingTestEntityBuilderFluentAPI() {
            // Given & When
            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withCustomTextValue("Space Gray")
                    .withCustomDecimalValue(new BigDecimal("256.00"))
                    .withCustomBooleanValue(true)
                    .withCustomDateValue(LocalDate.of(2024, 1, 15))
                    .build();

            // Then
            assertThat(variantProperty.getCustomValueText()).isEqualTo("Space Gray");
            assertThat(variantProperty.getCustomValueDecimal()).isEqualTo(new BigDecimal("256.00"));
            assertThat(variantProperty.getCustomValueBoolean()).isTrue();
            assertThat(variantProperty.getCustomValueDate()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(variantProperty.getId()).isNotNull();
        }

        @Test
        @DisplayName("Should create VariantProperty with convenience methods")
        void shouldCreateVariantPropertyWithConvenienceMethods() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();

            // When - using convenience methods
            VariantProperty colorProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .forColor("Midnight Blue")
                    .build();

            VariantProperty sizeProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .forSize("Large")
                    .build();

            VariantProperty weightProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .forWeight(new BigDecimal("2.5"))
                    .build();

            // Then
            assertThat(colorProperty.getCustomValueText()).isEqualTo("Midnight Blue");
            assertThat(colorProperty.getProperty().getName()).isEqualTo("Color");
            assertThat(colorProperty.getProperty().getDataType()).isEqualTo(PropertyDataType.TEXT);

            assertThat(sizeProperty.getCustomValueText()).isEqualTo("Large");
            assertThat(sizeProperty.getProperty().getName()).isEqualTo("Size");

            assertThat(weightProperty.getCustomValueDecimal()).isEqualTo(new BigDecimal("2.5"));
            assertThat(weightProperty.getProperty().getName()).isEqualTo("Weight");
            assertThat(weightProperty.getProperty().getUnit()).isEqualTo("kg");
            assertThat(weightProperty.getProperty().getDataType()).isEqualTo(PropertyDataType.NUMBER);
        }

        @Test
        @DisplayName("Should create VariantProperty with PropertyOption reference")
        void shouldCreateVariantPropertyWithPropertyOptionReference() {
            // Given
            Property colorProperty = TestEntityBuilder.property()
                    .withName("Color")
                    .asTextProperty()
                    .build();

            DefaultPropertyOption redOption = TestEntityBuilder.propertyOption()
                    .withProperty(colorProperty)
                    .withTextValue("Red")
                    .withDisplayText("Bright Red")
                    .build();

            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();

            // When
            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(colorProperty)
                    .withPropertyOption(redOption)
                    .build();

            // Then
            assertThat(variantProperty.getPropertyOption()).isEqualTo(redOption);
            assertThat(variantProperty.getProperty()).isEqualTo(colorProperty);
            assertThat(variantProperty.getCustomValueText()).isNull(); // Should use option, not custom value
        }

        @Test
        @DisplayName("Should allow null PropertyOption for custom values")
        void shouldAllowNullPropertyOptionForCustomValues() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            Property customProperty = TestEntityBuilder.property()
                    .withName("Custom Color")
                    .asTextProperty()
                    .build();

            // When
            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(customProperty)
                    .withPropertyOption(null) // Explicitly null
                    .withCustomTextValue("Custom Turquoise")
                    .build();

            // Then
            assertThat(variantProperty.getPropertyOption()).isNull();
            assertThat(variantProperty.getCustomValueText()).isEqualTo("Custom Turquoise");
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should implement equals and hashCode based on ID only")
        void shouldImplementEqualsAndHashCodeBasedOnIdOnly() {
            // Given
            UUID id = UUID.randomUUID();
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();

            VariantProperty variantProperty1 = TestEntityBuilder.variantProperty()
                    .withId(id)
                    .withVariant(scenario.variant)
                    .withCustomTextValue("Value1")
                    .build();

            VariantProperty variantProperty2 = TestEntityBuilder.variantProperty()
                    .withId(id) // Same ID
                    .withVariant(scenario.variant)
                    .withCustomTextValue("Value2") // Different value
                    .build();

            VariantProperty variantProperty3 = TestEntityBuilder.variantProperty()
                    .withId(UUID.randomUUID()) // Different ID
                    .withVariant(scenario.variant)
                    .withCustomTextValue("Value1") // Same value as first
                    .build();

            // Then
            assertThat(variantProperty1).isEqualTo(variantProperty2); // Same ID
            assertThat(variantProperty1).isNotEqualTo(variantProperty3); // Different ID
            assertThat(variantProperty1.hashCode()).isEqualTo(variantProperty2.hashCode());
            assertThat(variantProperty1.hashCode()).isNotEqualTo(variantProperty3.hashCode());
        }

        @Test
        @DisplayName("Should handle null values in equals comparison")
        void shouldHandleNullValuesInEqualsComparison() {
            // Given
            UUID id = UUID.randomUUID();

            VariantProperty variantProperty1 = TestEntityBuilder.variantProperty()
                    .withId(id)
                    .withCustomTextValue(null)
                    .withCustomDecimalValue(null)
                    .build();

            VariantProperty variantProperty2 = TestEntityBuilder.variantProperty()
                    .withId(id)
                    .withCustomTextValue("Some value")
                    .withCustomDecimalValue(new BigDecimal("100"))
                    .build();

            // Then - should be equal because same ID, regardless of other field values
            assertThat(variantProperty1).isEqualTo(variantProperty2);
            assertThat(variantProperty1.hashCode()).isEqualTo(variantProperty2.hashCode());
        }
    }

    @Nested
    @DisplayName("Data Type Validation Tests")
    class DataTypeValidationTests {

        @Test
        @DisplayName("Should handle all supported data types")
        void shouldHandleAllSupportedDataTypes() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            LocalDate testDate = LocalDate.of(2024, 6, 15);
            BigDecimal testDecimal = new BigDecimal("999.99");

            // When
            VariantProperty textProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withCustomTextValue("Sample text value")
                    .build();

            VariantProperty numberProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withCustomDecimalValue(testDecimal)
                    .build();

            VariantProperty booleanProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withCustomBooleanValue(false)
                    .build();

            VariantProperty dateProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withCustomDateValue(testDate)
                    .build();

            // Then
            assertThat(textProperty.getCustomValueText()).isEqualTo("Sample text value");
            assertThat(textProperty.getCustomValueDecimal()).isNull();
            assertThat(textProperty.getCustomValueBoolean()).isNull();
            assertThat(textProperty.getCustomValueDate()).isNull();

            assertThat(numberProperty.getCustomValueDecimal()).isEqualTo(testDecimal);
            assertThat(numberProperty.getCustomValueText()).isNull();

            assertThat(booleanProperty.getCustomValueBoolean()).isFalse();
            assertThat(booleanProperty.getCustomValueText()).isNull();

            assertThat(dateProperty.getCustomValueDate()).isEqualTo(testDate);
            assertThat(dateProperty.getCustomValueText()).isNull();
        }

        @Test
        @DisplayName("Should handle decimal precision correctly")
        void shouldHandleDecimalPrecisionCorrectly() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            BigDecimal preciseDecimal = new BigDecimal("12345678.99"); // 10 digits total, 2 decimal places

            // When
            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withCustomDecimalValue(preciseDecimal)
                    .build();

            // Then
            assertThat(variantProperty.getCustomValueDecimal()).isEqualTo(preciseDecimal);
            assertThat(variantProperty.getCustomValueDecimal().scale()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent variant characteristics correctly")
        void shouldRepresentVariantCharacteristicsCorrectly() {
            // Given - Electronics scenario
            TestScenarios.ElectronicsScenario scenario = TestScenarios.createElectronicsScenario();

            // When - create variant properties that describe iPhone characteristics
            VariantProperty storage = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.iPhone128GB)
                    .withProperty(scenario.storageProperty)
                    .withPropertyOption(scenario.storage128GB)
                    .build();

            VariantProperty color = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.iPhone128GB)
                    .withProperty(scenario.colorProperty)
                    .withPropertyOption(scenario.colorBlack)
                    .build();

            // Then - variant properties should correctly describe the product variant
            assertThat(storage.getPropertyOption().getValueText()).isEqualTo("128GB");
            assertThat(storage.getPropertyOption().getDisplayText()).isEqualTo("128 GB Storage");
            assertThat(color.getPropertyOption().getValueText()).isEqualTo("Black");
            assertThat(color.getPropertyOption().getDisplayText()).isEqualTo("Space Black");

            // Verify the relationships
            assertThat(storage.getVariant().getSku()).isEqualTo("IP15-128-BLK");
            assertThat(storage.getProperty().getName()).isEqualTo("Storage");
            assertThat(storage.getProperty().isRequired()).isTrue();
            assertThat(storage.getProperty().isFilterable()).isTrue();
        }

        @Test
        @DisplayName("Should handle mixed property option and custom value approach")
        void shouldHandleMixedPropertyOptionAndCustomValueApproach() {
            // Given - a scenario where some properties have predefined options, others are custom
            TestScenarios.ClothingScenario scenario = TestScenarios.createClothingScenario();

            // When - create properties using both approaches
            VariantProperty standardSize = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.tShirtSmallRed)
                    .withProperty(scenario.sizeProperty)
                    .withPropertyOption(scenario.sizeSmall) // Using predefined option
                    .build();

            VariantProperty customColor = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.tShirtSmallRed)
                    .forColor("Crimson Red") // Using custom value
                    .build();

            // Then - both approaches should work for the same variant
            assertThat(standardSize.getPropertyOption()).isNotNull();
            assertThat(standardSize.getPropertyOption().getValueText()).isEqualTo("S");
            assertThat(standardSize.getCustomValueText()).isNull();

            assertThat(customColor.getPropertyOption()).isNull();
            assertThat(customColor.getCustomValueText()).isEqualTo("Crimson Red");
            assertThat(customColor.getProperty().getName()).isEqualTo("Color");
        }
    }
}