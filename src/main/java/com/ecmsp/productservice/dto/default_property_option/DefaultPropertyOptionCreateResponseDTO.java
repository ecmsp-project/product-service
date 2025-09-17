package com.ecmsp.productservice.dto.default_property_option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultPropertyOptionCreateResponseDTO {
    private UUID id;
}
