package com.example.ride

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RideRepository : JpaRepository<Ride, UUID> {
    // TODO: Add a new endpoint /rides/riders/{riderId}
//    fun getRideByRiderId(riderId: String): List<Ride>
}