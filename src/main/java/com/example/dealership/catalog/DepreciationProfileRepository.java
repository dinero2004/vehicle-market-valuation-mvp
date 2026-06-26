package com.example.dealership.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepreciationProfileRepository extends JpaRepository<DepreciationProfile, Long> {

    Optional<DepreciationProfile> findFirstByBrandIgnoreCaseAndModelIgnoreCase(String brand, String model);

    Optional<DepreciationProfile> findFirstByBrandIgnoreCaseAndModelIsNull(String brand);

    Optional<DepreciationProfile> findFirstByBrandIsNullAndModelIsNull();
}
