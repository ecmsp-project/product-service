package com.ecmsp.productservice.dto.variant_reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantReservationCreateRequestDTO {
    private UUID variantId;
    private UUID reservationId;
    private Integer quantity;
}
