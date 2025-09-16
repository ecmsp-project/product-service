package com.ecmsp.productservice.dto.property_option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyValueResponseDTO {
    private UUID id;
    private String value;
    private UUID attributeId;
}