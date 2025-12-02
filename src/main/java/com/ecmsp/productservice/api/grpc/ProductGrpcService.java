package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.*;
import com.ecmsp.productservice.dto.product.ProductCreateRequestDTO;
import com.ecmsp.productservice.dto.product.ProductCreateResponseDTO;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private static final Logger logger = Logger.getLogger(ProductGrpcService.class.getName());

    private final ProductService productService;

    public ProductGrpcService(ProductService productService, VariantService variantService) {
        this.productService = productService;
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        UUID productId = UUID.fromString(request.getId());
        productService.deleteProduct(productId);

        DeleteProductResponse response = DeleteProductResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<CreateProductResponse> responseObserver) {
        UUID categoryId = UUID.fromString(request.getCategoryId());
        BigDecimal approximatePrice = new BigDecimal(request.getApproximatePrice().getValue());
        BigDecimal deliveryPrice = new BigDecimal(request.getDeliveryPrice().getValue());

        ProductCreateResponseDTO productCreateResponse = productService.createProduct(
                ProductCreateRequestDTO.builder()
                        .name(request.getName())
                        .categoryId(categoryId)
                        .approximatePrice(approximatePrice)
                        .deliveryPrice(deliveryPrice)
                        .description(request.getDescription())
                        .build()
        );

        CreateProductResponse response = CreateProductResponse.newBuilder()
                .setId(productCreateResponse.getId().toString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
