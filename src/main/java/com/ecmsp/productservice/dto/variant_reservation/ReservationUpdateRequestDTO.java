package com.ecmsp.productservice.dto.variant_reservation;

import com.ecmsp.productservice.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationUpdateRequestDTO {
    private UUID reservationId;
    private ReservationStatus status;

}
