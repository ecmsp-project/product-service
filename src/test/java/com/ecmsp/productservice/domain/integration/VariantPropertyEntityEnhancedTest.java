package com.ecmsp.productservice.domain.integration;

import com.ecmsp.productservice.domain.*;
import com.ecmsp.productservice.repository.VariantPropertyRepository;
import com.ecmsp.productservice.testutil.TestEntityBuilder;
import com.ecmsp.productservice.testutil.TestScenarios;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("VariantProperty Entity Enhanced Integration Tests")
class VariantPropertyEntityEnhancedTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VariantPropertyRepository variantPropertyRepository;

    @Nested
    @DisplayName("Using TestEntityBuilder")
    class TestEntityBuilderTests {

        @Test
        @DisplayName("Should create VariantProperty with fluent builder API")
        void shouldCreateVariantPropertyWithFluentBuilder() {
            // Given - using fluent builder
            Category category = TestEntityBuilder.category()
                    .withName("Electronics")
                    .build();
            entityManager.persistAndFlush(category);

            Product product = TestEntityBuilder.product()
                    .withCategory(category)
                    .withName("iPhone")
                    .build();
            entityManager.persistAndFlush(product);

            Variant variant = TestEntityBuilder.variant()
                    .withProduct(product)
                    .withSku("IP15-001")
                    .inStock()
                    .build();
            entityManager.persistAndFlush(variant);

            Property colorProperty = TestEntityBuilder.property()
                    .withName("Color")
                    .withCategory(category)
                    .asTextProperty()
                    .filterable()
                    .required()
                    .build();
            entityManager.persistAndFlush(colorProperty);

            // When - create variant property with builder
            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(variant)
                    .withProperty(colorProperty)
                    .withCustomTextValue("Space Gray")
                    .build();

            VariantProperty saved = variantPropertyRepository.save(variantProperty);
            entityManager.flush();
            entityManager.clear();

            // Then
            VariantProperty found = variantPropertyRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getCustomValueText()).isEqualTo("Space Gray");
            assertThat(found.getVariant().getSku()).isEqualTo("IP15-001");
            assertThat(found.getProperty().getName()).isEqualTo("Color");
        }

        @Test
        @DisplayName("Should create VariantProperty for color using convenience method")
        void shouldCreateVariantPropertyForColorUsingConvenienceMethod() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            // When - using convenience method
            VariantProperty colorProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .forColor("Red")
                    .build();

            // Persist the property that was auto-created
            entityManager.persistAndFlush(colorProperty.getProperty());
            VariantProperty saved = variantPropertyRepository.save(colorProperty);
            entityManager.flush();
            entityManager.clear();

            // Then
            VariantProperty found = variantPropertyRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getCustomValueText()).isEqualTo("Red");
            assertThat(found.getProperty().getName()).isEqualTo("Color");
            assertThat(found.getProperty().getDataType()).isEqualTo(PropertyDataType.TEXT);
        }

        @Test
        @DisplayName("Should create VariantProperty for weight using convenience method")
        void shouldCreateVariantPropertyForWeightUsingConvenienceMethod() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            BigDecimal weight = new BigDecimal("2.5");

            // When - using convenience method for weight
            VariantProperty weightProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .forWeight(weight)
                    .build();

            // Persist the property that was auto-created
            entityManager.persistAndFlush(weightProperty.getProperty());
            VariantProperty saved = variantPropertyRepository.save(weightProperty);
            entityManager.flush();
            entityManager.clear();

            // Then
            VariantProperty found = variantPropertyRepository.findById(saved.getId()).orElseThrow();
            assertThat(found.getCustomValueDecimal()).isEqualTo(weight);
            assertThat(found.getProperty().getName()).isEqualTo("Weight");
            assertThat(found.getProperty().getUnit()).isEqualTo("kg");
            assertThat(found.getProperty().getDataType()).isEqualTo(PropertyDataType.NUMBER);
        }

        @Test
        @DisplayName("Should handle different data types in one test")
        void shouldHandleDifferentDataTypesInOneTest() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            Property textProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .asTextProperty()
                    .withName("Description")
                    .build();

            Property numberProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .asNumberProperty()
                    .withName("Quantity")
                    .build();

            Property booleanProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .asBooleanProperty()
                    .withName("IsAvailable")
                    .build();

            Property dateProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .asDateProperty()
                    .withName("ReleaseDate")
                    .build();

            entityManager.persistAndFlush(textProperty);
            entityManager.persistAndFlush(numberProperty);
            entityManager.persistAndFlush(booleanProperty);
            entityManager.persistAndFlush(dateProperty);

            LocalDate releaseDate = LocalDate.of(2024, 12, 25);
            BigDecimal quantity = new BigDecimal("100");

            // When - create variant properties for different data types
            VariantProperty textVP = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(textProperty)
                    .withCustomTextValue("Premium quality")
                    .build();

            VariantProperty numberVP = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(numberProperty)
                    .withCustomDecimalValue(quantity)
                    .build();

            VariantProperty booleanVP = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(booleanProperty)
                    .withCustomBooleanValue(true)
                    .build();

            VariantProperty dateVP = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(dateProperty)
                    .withCustomDateValue(releaseDate)
                    .build();

            variantPropertyRepository.saveAll(List.of(textVP, numberVP, booleanVP, dateVP));
            entityManager.flush();
            entityManager.clear();

            // Then - verify all properties were saved correctly
            List<VariantProperty> found = variantPropertyRepository.findByVariant(scenario.variant);
            assertThat(found).hasSize(4);

            VariantProperty foundText = found.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Description"))
                    .findFirst().orElseThrow();
            assertThat(foundText.getCustomValueText()).isEqualTo("Premium quality");

            VariantProperty foundNumber = found.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Quantity"))
                    .findFirst().orElseThrow();
            assertThat(foundNumber.getCustomValueDecimal()).isEqualTo(quantity);

            VariantProperty foundBoolean = found.stream()
                    .filter(vp -> vp.getProperty().getName().equals("IsAvailable"))
                    .findFirst().orElseThrow();
            assertThat(foundBoolean.getCustomValueBoolean()).isTrue();

            VariantProperty foundDate = found.stream()
                    .filter(vp -> vp.getProperty().getName().equals("ReleaseDate"))
                    .findFirst().orElseThrow();
            assertThat(foundDate.getCustomValueDate()).isEqualTo(releaseDate);
        }
    }

    @Nested
    @DisplayName("Using TestScenarios")
    class TestScenariosTests {

        @Test
        @DisplayName("Should work with complete Electronics scenario")
        void shouldWorkWithCompleteElectronicsScenario() {
            // Given - complete realistic scenario
            TestScenarios.ElectronicsScenario scenario = TestScenarios.createElectronicsScenario();

            // Persist all entities in proper order
            entityManager.persistAndFlush(scenario.electronicsCategory);
            entityManager.persistAndFlush(scenario.smartphonesCategory);
            entityManager.persistAndFlush(scenario.iPhone);
            entityManager.persistAndFlush(scenario.iPhone128GB);
            entityManager.persistAndFlush(scenario.iPhone256GB);
            entityManager.persistAndFlush(scenario.storageProperty);
            entityManager.persistAndFlush(scenario.colorProperty);
            entityManager.persistAndFlush(scenario.storage128GB);
            entityManager.persistAndFlush(scenario.storage256GB);
            entityManager.persistAndFlush(scenario.colorBlack);
            entityManager.persistAndFlush(scenario.colorWhite);

            // When - save variant properties
            variantPropertyRepository.saveAll(List.of(
                    scenario.iPhone128GBStorage,
                    scenario.iPhone128GBColor,
                    scenario.iPhone256GBStorage,
                    scenario.iPhone256GBColor
            ));
            entityManager.flush();
            entityManager.clear();

            // Then - verify the complete scenario works
            List<VariantProperty> iPhone128Properties = variantPropertyRepository
                    .findByVariant(scenario.iPhone128GB);
            List<VariantProperty> iPhone256Properties = variantPropertyRepository
                    .findByVariant(scenario.iPhone256GB);

            assertThat(iPhone128Properties).hasSize(2);
            assertThat(iPhone256Properties).hasSize(2);

            // Verify iPhone128GB has 128GB storage and black color
            VariantProperty storageVP = iPhone128Properties.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Storage"))
                    .findFirst().orElseThrow();
            assertThat(storageVP.getPropertyOption().getValueText()).isEqualTo("128GB");

            VariantProperty colorVP = iPhone128Properties.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Color"))
                    .findFirst().orElseThrow();
            assertThat(colorVP.getPropertyOption().getValueText()).isEqualTo("Black");
        }

        @Test
        @DisplayName("Should work with Clothing scenario using custom values")
        void shouldWorkWithClothingScenarioUsingCustomValues() {
            // Given - clothing scenario
            TestScenarios.ClothingScenario scenario = TestScenarios.createClothingScenario();

            // Persist all entities
            entityManager.persistAndFlush(scenario.clothingCategory);
            entityManager.persistAndFlush(scenario.shirtsCategory);
            entityManager.persistAndFlush(scenario.tShirt);
            entityManager.persistAndFlush(scenario.tShirtSmallRed);
            entityManager.persistAndFlush(scenario.tShirtLargeBlue);
            entityManager.persistAndFlush(scenario.sizeProperty);
            entityManager.persistAndFlush(scenario.colorProperty);
            entityManager.persistAndFlush(scenario.sizeSmall);
            entityManager.persistAndFlush(scenario.sizeLarge);

            // When - save variant properties (mix of options and custom values)
            variantPropertyRepository.saveAll(List.of(
                    scenario.tShirtSmallSize,      // Uses PropertyOption
                    scenario.tShirtSmallColor,     // Uses custom value
                    scenario.tShirtLargeSize,      // Uses PropertyOption
                    scenario.tShirtLargeColor      // Uses custom value
            ));
            entityManager.flush();
            entityManager.clear();

            // Then - verify mixed approach works
            List<VariantProperty> smallRedProperties = variantPropertyRepository
                    .findByVariant(scenario.tShirtSmallRed);
            List<VariantProperty> largeBlueProperties = variantPropertyRepository
                    .findByVariant(scenario.tShirtLargeBlue);

            assertThat(smallRedProperties).hasSize(2);
            assertThat(largeBlueProperties).hasSize(2);

            // Verify small red t-shirt
            VariantProperty smallSize = smallRedProperties.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Size"))
                    .findFirst().orElseThrow();
            assertThat(smallSize.getPropertyOption()).isNotNull();
            assertThat(smallSize.getPropertyOption().getValueText()).isEqualTo("S");

            VariantProperty redColor = smallRedProperties.stream()
                    .filter(vp -> vp.getProperty().getName().equals("Color"))
                    .findFirst().orElseThrow();
            assertThat(redColor.getPropertyOption()).isNull(); // Custom value, no option
            assertThat(redColor.getCustomValueText()).isEqualTo("Red");
        }

        @Test
        @DisplayName("Should handle property option vs custom value decision")
        void shouldHandlePropertyOptionVsCustomValueDecision() {
            // Given - simple scenario
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            // Create a property with predefined options
            Property sizeProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .withName("Size")
                    .asTextProperty()
                    .build();
            entityManager.persistAndFlush(sizeProperty);

            PropertyOption mediumOption = TestEntityBuilder.propertyOption()
                    .withProperty(sizeProperty)
                    .withTextValue("M")
                    .withDisplayText("Medium")
                    .build();
            entityManager.persistAndFlush(mediumOption);

            // When - create two variant properties: one with option, one with custom value
            VariantProperty withOption = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(sizeProperty)
                    .withPropertyOption(mediumOption) // Use predefined option
                    .build();

            VariantProperty withCustomValue = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(sizeProperty)
                    .withCustomTextValue("Extra Large") // Custom value not in options
                    .build();

            variantPropertyRepository.saveAll(List.of(withOption, withCustomValue));
            entityManager.flush();
            entityManager.clear();

            // Then - both should be valid
            List<VariantProperty> properties = variantPropertyRepository.findByVariant(scenario.variant);
            assertThat(properties).hasSize(2);

            VariantProperty foundWithOption = properties.stream()
                    .filter(vp -> vp.getPropertyOption() != null)
                    .findFirst().orElseThrow();
            assertThat(foundWithOption.getPropertyOption().getValueText()).isEqualTo("M");
            assertThat(foundWithOption.getCustomValueText()).isNull();

            VariantProperty foundWithCustom = properties.stream()
                    .filter(vp -> vp.getPropertyOption() == null)
                    .findFirst().orElseThrow();
            assertThat(foundWithCustom.getCustomValueText()).isEqualTo("Extra Large");
            assertThat(foundWithCustom.getPropertyOption()).isNull();
        }
    }

    @Nested
    @DisplayName("Repository Method Tests")
    class RepositoryMethodTests {

        @Test
        @DisplayName("Should find variant properties by variant using repository methods")
        void shouldFindVariantPropertiesByVariantUsingRepositoryMethods() {
            // Given - scenario with multiple properties
            TestScenarios.ElectronicsScenario scenario = TestScenarios.createElectronicsScenario();

            // Persist required entities
            entityManager.persistAndFlush(scenario.electronicsCategory);
            entityManager.persistAndFlush(scenario.smartphonesCategory);
            entityManager.persistAndFlush(scenario.iPhone);
            entityManager.persistAndFlush(scenario.iPhone128GB);
            entityManager.persistAndFlush(scenario.storageProperty);
            entityManager.persistAndFlush(scenario.colorProperty);
            entityManager.persistAndFlush(scenario.storage128GB);
            entityManager.persistAndFlush(scenario.colorBlack);

            variantPropertyRepository.saveAll(List.of(
                    scenario.iPhone128GBStorage,
                    scenario.iPhone128GBColor
            ));
            entityManager.flush();

            // When - use repository methods
            List<VariantProperty> byVariant = variantPropertyRepository
                    .findByVariant(scenario.iPhone128GB);
            List<VariantProperty> byVariantId = variantPropertyRepository
                    .findByVariantId(scenario.iPhone128GB.getId());
            List<VariantProperty> byProperty = variantPropertyRepository
                    .findByProperty(scenario.storageProperty);

            // Then - verify repository methods work
            assertThat(byVariant).hasSize(2);
            assertThat(byVariantId).hasSize(2);
            assertThat(byProperty).hasSize(1);

            assertThat(byVariant).containsExactlyInAnyOrderElementsOf(byVariantId);
        }

        @Test
        @DisplayName("Should find specific variant property by variant and property")
        void shouldFindSpecificVariantPropertyByVariantAndProperty() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            Property colorProperty = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .withName("Color")
                    .asTextProperty()
                    .build();
            entityManager.persistAndFlush(colorProperty);

            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(colorProperty)
                    .withCustomTextValue("Blue")
                    .build();
            variantPropertyRepository.save(variantProperty);
            entityManager.flush();

            // When
            var found = variantPropertyRepository
                    .findByVariantAndProperty(scenario.variant, colorProperty);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getCustomValueText()).isEqualTo("Blue");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should fail when trying to save VariantProperty without required variant")
        void shouldFailWhenTryingToSaveVariantPropertyWithoutRequiredVariant() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);

            Property property = TestEntityBuilder.property()
                    .withCategory(scenario.category)
                    .build();
            entityManager.persistAndFlush(property);

            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(null) // Invalid: null variant
                    .withProperty(property)
                    .withCustomTextValue("Test")
                    .build();

            // When & Then
            assertThatThrownBy(() -> {
                variantPropertyRepository.save(variantProperty);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("Should fail when trying to save VariantProperty without required property")
        void shouldFailWhenTryingToSaveVariantPropertyWithoutRequiredProperty() {
            // Given
            TestScenarios.SimpleProductScenario scenario = TestScenarios.createSimpleProductScenario();
            entityManager.persistAndFlush(scenario.category);
            entityManager.persistAndFlush(scenario.product);
            entityManager.persistAndFlush(scenario.variant);

            VariantProperty variantProperty = TestEntityBuilder.variantProperty()
                    .withVariant(scenario.variant)
                    .withProperty(null) // Invalid: null property
                    .withCustomTextValue("Test")
                    .build();

            // When & Then
            assertThatThrownBy(() -> {
                variantPropertyRepository.save(variantProperty);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}