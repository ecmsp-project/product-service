package com.ecmsp.productservice.api.grpc;

import com.ecmsp.product.v1.*;
import com.ecmsp.product.v1.reservation.v1.GetVariantsReservationResponse;
import com.ecmsp.productservice.domain.Variant;
import com.ecmsp.productservice.service.ProductService;
import com.ecmsp.productservice.service.VariantService;
import com.google.protobuf.Struct;
import com.google.type.Decimal;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@GrpcService
public class ProductDisplayGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    private static final Logger logger = Logger.getLogger(ProductDisplayGrpcService.class.getName());

    private final ProductService productService;
    private final VariantService variantService;

    public ProductDisplayGrpcService(ProductService productService, VariantService variantService) {
        this.productService = productService;
        this.variantService = variantService;
    }

    @Override
    public void getVariantsDetails(GetVariantsDetailsRequest request, StreamObserver<GetVariantsDetailsResponse> responseObserver) {
        logger.info("variant details request received");

        UUID productId = UUID.fromString(request.getProductId());

        List<Variant> variants = variantService.getVariantsByProductId(productId);

        List<VariantDetail> variantsDetails = variants.stream().map(item -> {
            return VariantDetail.newBuilder()
                    .setVariantId(item.getId().toString())

                    .setPrice(Decimal.newBuilder().setValue(item.getPrice().toString()).build())
                    .setStockQuantity(item.getStockQuantity())
                    .setImageUrl(item.getImageUrl())
                    .setDescription(item.getDescription())
                    .setAdditionalProperties(Struct.newBuilder().build())

                    .build();
        }).toList();

        GetVariantsDetailsResponse response = GetVariantsDetailsResponse.newBuilder()
                .addAllVariants(variantsDetails)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("variant details response sent");
    }

    @Override
    public void getProductsByQuery(GetProductsByQueryRequest request, StreamObserver<GetProductsByQueryResponse> responseObserver) {
        // TODO: implement when ElasticSearch is configured
    }

    @Override
    public void getProducts(GetProductsRequest request, StreamObserver<GetProductsResponse> responseObserver) {
        // TODO: implement pagination

        logger.info("get products (variants to display) request received");

        UUID categoryId = UUID.fromString(request.getCategoryId());
        List<Variant> variants = variantService.getOneVariantPerProductByCategoryId(categoryId);

        List<VariantDetail> variantsDetails = variants.stream().map(item -> {
            return VariantDetail.newBuilder()
                    .setVariantId(item.getId().toString())

                    .setStockQuantity(item.getStockQuantity())
                    .setImageUrl(item.getImageUrl())
                    .setDescription(item.getDescription())
                    .setPrice(Decimal.newBuilder().setValue(item.getPrice().toString()).build())
                    .setAdditionalProperties(Struct.newBuilder().build())

                    .build();
        }).toList();

        List<ProductRepresentation> productsRepresentation = variantsDetails.stream().map(item -> {
            return ProductRepresentation.newBuilder()
                    .setProductId("TODO / Change proto definition")
                    .setVariant(item)
                    .build();
        }).toList();

        GetProductsResponse response = GetProductsResponse.newBuilder()
                .addAllProductsRepresentation(productsRepresentation)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        logger.info("get products (variants to display) response sent");
    }
}
