package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.category.v1.*;
import com.ecmsp.productservice.dto.category.CategoryCreateRequestDTO;
import com.ecmsp.productservice.dto.category.CategoryCreateResponseDTO;
import com.ecmsp.productservice.dto.rest.category.CreateCategoryRequestDTO;
import com.ecmsp.productservice.service.CategoryService;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class CategoryGrpcService extends CategoryServiceGrpc.CategoryServiceImplBase {
    private final CategoryService categoryService;

    public CategoryGrpcService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void deleteCategory(DeleteCategoryRequest request, StreamObserver<DeleteCategoryResponse> responseObserver) {
        super.deleteCategory(request, responseObserver);
    }

    @Override
    public void createCategory(CreateCategoryRequest request, StreamObserver<CreateCategoryResponse> responseObserver) {
        UUID parentCategoryId = UUID.fromString(request.getParentCategoryId());
        UUID childCategoryId = UUID.fromString(request.getChildCategoryId());

        CategoryCreateRequestDTO categoryCreateRequest = CategoryCreateRequestDTO.builder()
                .name(request.getName())
                .parentCategoryId(parentCategoryId)
                .childCategoryId(childCategoryId)
                .build();

        CategoryCreateResponseDTO categoryCreateResponse = switch (request.getCreateCategoryType()) {
            case CREATE_CATEGORY_TYPE_SPLIT, UNRECOGNIZED ->  categoryService.createCategorySplit(categoryCreateRequest);
            case CREATE_CATEGORY_TYPE_LEAF_UNSPECIFIED -> categoryService.createCategoryLeaf(categoryCreateRequest);
            case CREATE_CATEGORY_TYPE_ALL_SPLIT ->   categoryService.createCategoryAllSplit(categoryCreateRequest);
        };


    }
}
