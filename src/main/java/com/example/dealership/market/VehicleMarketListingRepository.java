package com.example.dealership.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleMarketListingRepository extends JpaRepository<VehicleMarketListing, Long> {

    @Query("""
            select listing
            from VehicleMarketListing listing
            where lower(listing.brand) = lower(:brand)
              and lower(listing.model) = lower(:model)
              and listing.manufactureYear between :minYear and :maxYear
              and listing.mileage between :minMileage and :maxMileage
            order by listing.price asc
            """)
    List<VehicleMarketListing> findSimilarListings(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("minYear") int minYear,
            @Param("maxYear") int maxYear,
            @Param("minMileage") int minMileage,
            @Param("maxMileage") int maxMileage
    );
}
