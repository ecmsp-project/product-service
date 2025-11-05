package com.ecmsp.productservice.dto.variant_reservation;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class VariantReservationResultDTO {
    private List<UUID> reservedVariantIds;
    private List<FailedReservationVariantDTO> failedVariants;
}
