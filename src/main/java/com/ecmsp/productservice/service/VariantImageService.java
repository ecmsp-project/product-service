package com.ecmsp.productservice.service;

import com.ecmsp.productservice.repository.VariantImageRepository;
import org.springframework.stereotype.Service;

@Service
public class VariantImageService {
    private final VariantImageRepository variantImageRepository;

    public VariantImageService(VariantImageRepository variantImageRepository) {
        this.variantImageRepository = variantImageRepository;
    }
}
