package com.ecmsp.productservice.testutil;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.stereotype.Component;

@Component
public class TestEntitiesGenerator {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category randomCategory() {
        Category category = Category
                .builder()
                .parentCategory(null)
                .name(TestDataGenerator.randomName())
                .build();

        return categoryRepository.save(category);
    }

}
