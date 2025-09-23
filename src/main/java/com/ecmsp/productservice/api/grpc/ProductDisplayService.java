package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.*;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@GrpcService
public class ProductDisplayService extends ProductServiceGrpc.ProductServiceImplBase {
    private static final Logger logger = Logger.getLogger(ProductDisplayService.class.getName());

    private final ProductService productService;
    private final VariantService variantService;

    public ProductDisplayService(ProductService productService, VariantService variantService) {
        this.productService = productService;
        this.variantService = variantService;
    }

    @Override
    public void getProductDetails(GetProductDetailsRequest request, StreamObserver<GetProductDetailsResponse> responseObserver) {
        logger.info("got a get product details request");

        String productId = request.getProductId();
        UUID productUUID = UUID.fromString(productId);

        // TODO: implement

        GetProductDetailsResponse response = GetProductDetailsResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("sent a get product details response");
    }

    @Override
    public void getProductsByQuery(GetProductsByQueryRequest request, StreamObserver<GetProductsByQueryResponse> responseObserver) {
        super.getProductsByQuery(request, responseObserver);
    }

    @Override
    public void getProducts(GetProductsRequest request, StreamObserver<GetProductsResponse> responseObserver) {
    }
}
