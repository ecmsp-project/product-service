package com.ecmsp.productservice.repository;

import com.ecmsp.productservice.domain.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VariantImageRepository extends JpaRepository<VariantImage, UUID> {

}
