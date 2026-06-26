package com.example.dealership.valuation;

import com.example.dealership.valuation.dto.CalculateValuationRequest;
import com.example.dealership.valuation.dto.ManualValuationRequest;
import com.example.dealership.valuation.dto.ManualValuationResponse;
import com.example.dealership.valuation.dto.ValuationResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/valuations")
public class ValuationController {

    private final ValuationService valuationService;

    public ValuationController(ValuationService valuationService) {
        this.valuationService = valuationService;
    }

    @PostMapping("/calculate")
    public ValuationResponse calculate(@Valid @RequestBody CalculateValuationRequest request) {
        return valuationService.calculateValuation(request);
    }

    @PostMapping("/manual")
    public ManualValuationResponse calculateManual(@Valid @RequestBody ManualValuationRequest request) {
        return valuationService.calculateManualValuation(request);
    }

    @GetMapping("/{vehicleId}")
    public ValuationResponse getLatestValuation(@PathVariable Long vehicleId) {
        return valuationService.getLatestValuation(vehicleId);
    }
}
