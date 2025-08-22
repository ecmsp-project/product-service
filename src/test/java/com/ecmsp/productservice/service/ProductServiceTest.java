package com.ecmsp.productservice.service;

import com.ecmsp.productservice.dto.ProductResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.ProductRepository;
import com.ecmsp.productservice.testutil.TestEntitiesGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

//@Component
//@ExtendWith(MockitoExtension.class)
//public class ProductServiceTest {
//
//    @InjectMocks
//    @Autowired
//    private ProductService productService;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Test
//    public void shouldReturnProductById() {
//
//        UUID goodId = UUID.randomUUID();
//        UUID badId = UUID.randomUUID();
//
//        when(productRepository.findById(goodId))
//                .thenReturn(Optional.of(TestEntitiesGenerator.randomProduct()));
//        when(productRepository.findById(badId))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> {
//            productService.getProductById(badId);
//        }).isInstanceOf(ResourceNotFoundException.class);
//
//        assertThat(productService.getProductById(goodId))
//                .isNotNull()
//                .extracting(ProductResponseDTO::getId)
//                .isNotNull();
//    }
//}
