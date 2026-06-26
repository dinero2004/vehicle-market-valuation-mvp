package com.example.dealership.market;

import com.example.dealership.valuation.ValuationService;
import com.example.dealership.valuation.dto.MarketListingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market-listings")
public class MarketListingController {

    private final ValuationService valuationService;

    public MarketListingController(ValuationService valuationService) {
        this.valuationService = valuationService;
    }

    @GetMapping("/similar/{vehicleId}")
    public List<MarketListingResponse> getSimilarListings(@PathVariable Long vehicleId) {
        return valuationService.getSimilarListings(vehicleId);
    }
}
