package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValueRequestDTO {
    @NotBlank(message = "Value cannot be blank")
    @Size(max = 255, message = "Value cannot exceed 255 characters")
    private String value;

    @NotNull(message = "Attribute ID is required")
    private UUID attributeId;
    // TODO: is this field required in DTOs? What if we want just to change AttributeValue's value? Check in other DTOs.
}