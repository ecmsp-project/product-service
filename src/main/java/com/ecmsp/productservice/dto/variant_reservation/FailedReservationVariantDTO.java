package com.ecmsp.productservice.dto.variant_reservation;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class FailedReservationVariantDTO {
    private UUID variantId;
    private int requestedQuantity;
    private int availableQuantity;
}
