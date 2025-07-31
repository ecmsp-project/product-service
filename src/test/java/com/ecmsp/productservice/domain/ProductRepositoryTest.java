package com.ecmsp.productservice.domain;

import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.testutil.TestDataGenerator;
import com.ecmsp.productservice.testutil.TestEntitiesGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Disabled
@DataJpaTest
@Import(TestEntitiesGenerator.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindById() {
        // given
        Product product = Product
                .builder()
                .name(TestDataGenerator.randomName())
                .approximatePrice(TestDataGenerator.randomPrice())
                .deliveryPrice(TestDataGenerator.randomPrice())
                .description(TestDataGenerator.randomDescription())
                .category(TestEntitiesGenerator.randomCategory())
                .build();


        // when
        productRepository.save(product);
        Optional<Product> found = productRepository.findById(product.getId());

        // then
        assertThat(found).isPresent();

        assertThat(
                found.map(Product::getId).orElseThrow()
        ).isEqualTo(product.getId());

        assertThat(
                found.map(Product::getName).orElse("")
        ).isEqualTo(product.getName());

        Category category = found.map(Product::getCategory).orElse(null);
        assertThat(category).isNotNull();
        System.out.println(category);
    }

    @Test
    void testSaveIncompleteProduct() {
        // given
        Product product = Product
                .builder()
                .category(null)
                .name("")
                .build();

        assertThatThrownBy(() -> productRepository.saveAndFlush(product));
    }
}
