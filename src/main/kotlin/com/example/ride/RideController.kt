package com.example.ride

import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/rides")
class RideController(private val rideService: RideService) {
    @GetMapping("/{id}")
    fun getRide(@PathVariable("id") rideId: UUID): RideResponseDTO {
        val ride = rideService.getRide(rideId)
        return RideResponseDTO.from(ride)
    }

    @PostMapping
    fun createRide(riderId: String, pickup: String, destination: String): Ride {
        return rideService.createRide(riderId=riderId, pickup=pickup, destination=destination)
    }
}