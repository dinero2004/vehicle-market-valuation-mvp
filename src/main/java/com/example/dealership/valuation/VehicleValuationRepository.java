package com.example.dealership.valuation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleValuationRepository extends JpaRepository<VehicleValuation, Long> {

    Optional<VehicleValuation> findFirstByVehicleIdOrderByValuationDateDesc(Long vehicleId);
}
