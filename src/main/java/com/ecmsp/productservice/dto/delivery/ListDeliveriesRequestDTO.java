package com.ecmsp.productservice.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListDeliveriesRequestDTO {
    private UUID variantId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
