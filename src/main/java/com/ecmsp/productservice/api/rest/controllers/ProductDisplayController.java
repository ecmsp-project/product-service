package com.ecmsp.productservice.api.rest.controllers;

import com.ecmsp.productservice.domain.Category;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.dto.category.CategoryResponseDTO;
import com.ecmsp.productservice.dto.rest.GetProductsRequestDTO;
import com.ecmsp.productservice.dto.rest.GetProductsResponseDTO;
import com.ecmsp.productservice.dto.rest.ProductRepresentationDTO;
import com.ecmsp.productservice.dto.rest.VariantDetailDTO;
import com.ecmsp.productservice.service.CategoryService;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController("/api/")
public class ProductDisplayController {
    private final ProductService productService;
    private final VariantService variantService;
    private final CategoryService categoryService;

    public ProductDisplayController(
            ProductService productService,
            VariantService variantService,
            CategoryService categoryService
    ) {
        this.productService = productService;
        this.variantService = variantService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public ResponseEntity<GetProductsResponseDTO> getProducts(@RequestBody GetProductsRequestDTO request) {
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;

        int pageNumber = 0;
        if (!request.pageToken().isEmpty()) {
            pageNumber = Integer.parseInt(request.pageToken());
        }

        // TODO: category name is not unique - remove it later
        CategoryResponseDTO category = categoryService.getCategoryByName(request.categoryName());

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Variant> page = variantService.getOneVariantPerProductByCategoryId(category.getId(), pageable);

        List<ProductRepresentationDTO> productRepresentationsDTO = page.map(
                item ->
                        ProductRepresentationDTO.builder()
                                .productId(item.getProduct().getId())
                                .variantDetail(
                                        VariantDetailDTO.builder()
                                                .variant_id(item.getId())
                                                .price(item.getPrice())
                                                .stockQuantity(item.getStockQuantity())
                                                .imageUrl(item.getImageUrl())
                                                .description(item.getDescription())
                                                .additionalProperties(item.getAdditionalProperties())
                                                .build()
                                )
                                .build()
        ).toList();

        GetProductsResponseDTO response = GetProductsResponseDTO.builder()
                .productsRepresentation(productRepresentationsDTO)
                .nextPageToken(String.valueOf(page.nextPageable().getPageNumber()))
                .build();

        return ResponseEntity.ok(response);
    }
}
