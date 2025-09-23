package com.ecmsp.productservice.service;

import com.ecmsp.productservice.repository.VariantReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class VariantReservationService {
    private final VariantReservationRepository variantReservationRepository;

    public VariantReservationService(VariantReservationRepository variantReservationRepository) {
        this.variantReservationRepository = variantReservationRepository;
    }


}

