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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

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
                        .name(item.getProduct().getName())
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
    // TODO: Add correct product search logic based on Query- for now it will be dummy data
    public GetProductsResponseDTO getProductsQueried(GetProductsRequestDTO request, String query) {
        List<ProductRepresentationDTO> productRepresentationsDTO = IntStream.range(0, 4)
                .mapToObj(i -> ProductRepresentationDTO.builder()
                        .productId(UUID.randomUUID())
                        .name("Mocked product " + (i + 1))
                        .variantDetail(
                                VariantDetailDTO.builder()
                                        .variant_id(UUID.randomUUID())
                                        .price(BigDecimal.valueOf(1000 + (i * 500L)))
                                        .stockQuantity(100 + (i * 200))
                                        .imageUrl("https://images.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg")
                                        .description("Description for product " + (i + 1) + query)
                                        .additionalProperties(null)
                                        .build()
                        )
                        .build())
                .toList();

        return GetProductsResponseDTO.builder()
                .productsRepresentation(productRepresentationsDTO)
                .nextPageNumber(1)
                .build();
    }


}
