package com.ecmsp.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal approximatePrice;
    private BigDecimal deliveryPrice;
    private String description;
    private Map<String, Object> info;
    private UUID categoryId;
    private String categoryName;
}