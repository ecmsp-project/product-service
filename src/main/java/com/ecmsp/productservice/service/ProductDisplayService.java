package com.ecmsp.productservice.service;

import com.ecmsp.productservice.domain.PropertyRole;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.domain.VariantImage;
import com.ecmsp.productservice.dto.VariantImageResponseDTO;
import com.ecmsp.productservice.dto.property.PropertyResponseDTO;
import com.ecmsp.productservice.dto.rest.GetProductsRequestDTO;
import com.ecmsp.productservice.dto.rest.GetProductsResponseDTO;
import com.ecmsp.productservice.dto.rest.ProductRepresentationDTO;
import com.ecmsp.productservice.dto.rest.VariantDetailDTO;
import com.ecmsp.productservice.dto.rest.product.GetProductsFilteredRequestDTO;
import com.ecmsp.productservice.dto.rest.variant.GetVariantResponseDTO;
import com.ecmsp.productservice.dto.variant.VariantResponseDTO;
import com.ecmsp.productservice.dto.variant_property.VariantPropertyResponseDTO;
import com.ecmsp.productservice.exception.ResourceNotFoundException;
import com.ecmsp.productservice.repository.DefaultPropertyOptionRepository;
import com.ecmsp.productservice.repository.ProductDisplayRepository;
import com.ecmsp.productservice.repository.VariantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductDisplayService {
    private final ProductService productService;
    private final VariantService variantService;
    private final CategoryService categoryService;
    private final DefaultPropertyOptionService defaultPropertyOptionService;
    private final VariantPropertyService variantPropertyService;
    private final DefaultPropertyOptionRepository defaultPropertyOptionRepository;
    private final VariantRepository variantRepository;
    private final ProductDisplayRepository productDisplayRepository;
    private final VariantImageService variantImageService;
    private final PropertyService propertyService;

    public ProductDisplayService(
            ProductService productService,
            VariantService variantService,
            VariantPropertyService variantPropertyService,
            CategoryService categoryService,
            VariantImageService variantImageService,
            DefaultPropertyOptionService defaultPropertyOptionService,
            PropertyService propertyService,
            DefaultPropertyOptionRepository defaultPropertyOptionRepository,
            VariantRepository variantRepository,
            ProductDisplayRepository productDisplayRepository) {
        this.productService = productService;
        this.variantService = variantService;
        this.categoryService = categoryService;
        this.defaultPropertyOptionService = defaultPropertyOptionService;
        this.variantPropertyService = variantPropertyService;
        this.defaultPropertyOptionRepository = defaultPropertyOptionRepository;
        this.variantRepository = variantRepository;
        this.productDisplayRepository = productDisplayRepository;
        this.variantImageService = variantImageService;
        this.propertyService = propertyService;
    }

    public GetProductsResponseDTO getProducts(GetProductsRequestDTO request, UUID categoryId) {
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        int pageNumber = request.pageNumber() != null ? request.pageNumber() : 0;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Variant> page = productDisplayRepository.findOneVariantPerProductByCategoryId(categoryId, pageable);
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
        List<ProductRepresentationDTO> productRepresentationsDTO = page.map(item -> {
                    List<VariantImageResponseDTO> variantImages = variantImageService.convertVariantImagesToDto(item.getVariantImages());

                    return ProductRepresentationDTO.builder()
                            .productId(item.getProduct().getId())
                            .variantDetail(
                                    VariantDetailDTO.builder()
                                            .variantId(item.getId())
                                            .price(item.getPrice())
                                            .stockQuantity(item.getStockQuantity())
                                            .variantImages(variantImages)
                                            .description(item.getDescription())
                                            .additionalProperties(item.getAdditionalProperties())
                                            .build()
                            )
                            .build();
                }).toList();

        return GetProductsResponseDTO.builder()
                .productsRepresentation(productRepresentationsDTO)
                .nextPageNumber(pageNumber + 1)
                .build();
    }

    public GetVariantResponseDTO getAllVariantDetails(UUID variantId) {
        Variant primaryVariantEntity = variantService.getVariantEntityById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
        List<VariantImageResponseDTO> variantImages = variantImageService.convertVariantImagesToDto(primaryVariantEntity.getVariantImages());

        List<VariantResponseDTO> variants = variantService.getVariantsByProductId(primaryVariantEntity.getProduct().getId());

        List<PropertyResponseDTO> propertyResponseDTOs = propertyService.getPropertiesByCategoryIdAndRole(
                primaryVariantEntity.getProduct().getCategory().getId(),
                PropertyRole.SELECTABLE
        );

        List<Map<String, Object>> variantsWithPropertiesMap = new ArrayList<>();

        for (VariantResponseDTO variantResponse: variants) {
            List<VariantPropertyResponseDTO> variantProperties = variantPropertyService.getVariantPropertiesByVariantIdAndPropertyRole(
                    variantResponse.getId(),
                    PropertyRole.SELECTABLE
            );

            Map<String, Object> variantPropertiesMap = new HashMap<>();
            for (VariantPropertyResponseDTO variantPropertyResponse: variantProperties) {
                variantPropertiesMap.put(variantPropertyResponse.getPropertyName(), variantPropertyResponse.getDisplayText());
            }
            variantPropertiesMap.put("variantId", variantResponse.getId().toString());
            variantsWithPropertiesMap.add(variantPropertiesMap);
        }

        return GetVariantResponseDTO.builder()
                .variant(
                        VariantDetailDTO.builder()
                                .variantId(primaryVariantEntity.getId())
                                .price(primaryVariantEntity.getPrice())
                                .stockQuantity(primaryVariantEntity.getStockQuantity())
                                .variantImages(variantImages)
                                .description(primaryVariantEntity.getDescription())
                                .additionalProperties(primaryVariantEntity.getAdditionalProperties())
                                .build()
                )
                .selectablePropertyNames(propertyResponseDTOs.stream().map(PropertyResponseDTO::getName).toList())
                .allVariants(variantsWithPropertiesMap)
                .build();
    }

    public GetVariantResponseDTO getVariantDetails(UUID variantId) {

        VariantResponseDTO variant = variantService.getVariantById(variantId);
        List<VariantImage> variantImageEntities = variantImageService.getVariantImagesByVariantId(variantId);
        List<VariantImageResponseDTO> variantImages = variantImageService.convertVariantImagesToDto(variantImageEntities);

        return GetVariantResponseDTO.builder()
                .variant(
                        VariantDetailDTO.builder()
                                .variantId(variant.getId())
                                .price(variant.getPrice())
                                .stockQuantity(variant.getStockQuantity())
                                .variantImages(variantImages)
                                .description(variant.getDescription())
                                .additionalProperties(variant.getAdditionalProperties())
                                .build()
                )
                .build();
    }
}
