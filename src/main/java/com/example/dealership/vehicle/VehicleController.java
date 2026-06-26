package com.example.dealership.vehicle;

import com.example.dealership.vehicle.dto.VehicleResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public List<VehicleResponse> getVehicles() {
        return vehicleRepository.findAll(Sort.by("id")).stream()
                .map(VehicleResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{vehicleId}")
    public VehicleResponse getVehicle(@PathVariable Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(VehicleResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found: " + vehicleId));
    }
}
