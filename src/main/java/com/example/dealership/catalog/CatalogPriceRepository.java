package com.example.dealership.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatalogPriceRepository extends JpaRepository<CatalogPrice, Long> {

    @Query("""
            select catalogPrice
            from CatalogPrice catalogPrice
            where lower(catalogPrice.brand) = lower(:brand)
              and lower(catalogPrice.model) = lower(:model)
            order by
              case
                when lower(catalogPrice.version) = lower(:version) then 0
                when lower(catalogPrice.version) like lower(concat('%', :version, '%')) then 1
                when lower(:version) like lower(concat('%', catalogPrice.version, '%')) then 2
                else 3
              end,
              abs(catalogPrice.manufactureYear - :year)
            """)
    List<CatalogPrice> findBestMatches(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("version") String version,
            @Param("year") Integer year
    );
}
