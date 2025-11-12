package com.ecmsp.productservice.dto.variant_reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantsReservationCreateRequestDTO {

    @NotNull
    private UUID reservationId;

    @NotEmpty
    private Map<UUID, Integer> variants;
}
