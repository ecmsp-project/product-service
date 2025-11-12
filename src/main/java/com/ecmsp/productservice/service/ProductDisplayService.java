package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.dto.rest.GetProductsRequestDTO;
import com.ecmsp.productservice.dto.rest.GetProductsResponseDTO;
import com.ecmsp.productservice.dto.rest.ProductRepresentationDTO;
import com.ecmsp.productservice.dto.rest.VariantDetailDTO;
import com.ecmsp.productservice.dto.rest.product.GetProductsFilteredRequestDTO;
import com.ecmsp.productservice.repository.DefaultPropertyOptionRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProductDisplayService {
    private final ProductService productService;
    private final VariantService variantService;
    private final CategoryService categoryService;
    private final DefaultPropertyOptionService defaultPropertyOptionService;
    private final VariantPropertyService variantPropertyService;
    private final DefaultPropertyOptionRepository defaultPropertyOptionRepository;
    private final VariantRepository variantRepository;

    public ProductDisplayService(
            ProductService productService,
            VariantService variantService,
            VariantPropertyService variantPropertyService,
            CategoryService categoryService,
            DefaultPropertyOptionService defaultPropertyOptionService,
            DefaultPropertyOptionRepository defaultPropertyOptionRepository, VariantRepository variantRepository) {
        this.productService = productService;
        this.variantService = variantService;
        this.categoryService = categoryService;
        this.defaultPropertyOptionService = defaultPropertyOptionService;
        this.variantPropertyService = variantPropertyService;
        this.defaultPropertyOptionRepository = defaultPropertyOptionRepository;
        this.variantRepository = variantRepository;
    }

    public GetProductsResponseDTO getProducts(GetProductsRequestDTO request, UUID categoryId) {
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        int pageNumber = request.pageNumber() != null ? request.pageNumber() : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Variant> page = variantService.getOneVariantPerProductByCategoryId(categoryId, pageable);

        return mapVariantsToGetProductRepresentationDTO(pageNumber, page);
    }

    public GetProductsResponseDTO getProductsFiltered(GetProductsFilteredRequestDTO request) {
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        int pageNumber = request.pageNumber() != null ? request.pageNumber() : 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<UUID> defaultPropertyOptionIds = request.defaultPropertyOptionIds();

        Page<Variant> page = variantRepository.findByDefaultPropertyOptionIds(
                defaultPropertyOptionIds,
                request.minPrice(),
                request.maxPrice(),
                pageable
        );

        return mapVariantsToGetProductRepresentationDTO(pageNumber, page);
    }

    private GetProductsResponseDTO mapVariantsToGetProductRepresentationDTO(int pageNumber, Page<Variant> page) {
        List<ProductRepresentationDTO> productRepresentationsDTO = page.map(item ->
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

        return GetProductsResponseDTO.builder()
                .productsRepresentation(productRepresentationsDTO)
                .nextPageNumber(pageNumber + 1)
                .build();
    }


}
