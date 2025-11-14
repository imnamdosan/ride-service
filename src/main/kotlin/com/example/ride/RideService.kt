package com.example.ride

import org.springframework.stereotype.Service
import java.util.*

@Service
class RideService(private val rideRepository: RideRepository) {
    fun createRide(riderId: String, pickup: String, destination: String): Ride {
        val ride = Ride(riderId = riderId, pickup=pickup, destination = destination)
        return rideRepository.save(ride)
    }

    fun getRide(id: UUID): Ride {
        return rideRepository.getRideById(id)
    }

    fun getRidesByRider(riderId: String): List<Ride> {
        return rideRepository.getRideByRiderId(riderId)
    }
}