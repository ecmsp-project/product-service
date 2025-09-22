package com.ecmsp.productservice.dto.variant_property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantPropertyCreateResponseDTO {
    private UUID id;
}
