package com.ecmsp.productservice.dto.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDeliveryRequestDTO {
    @NotEmpty(message = "Delivery items cannot be empty")
    @Valid
    private List<DeliveryItemDTO> items;
}
