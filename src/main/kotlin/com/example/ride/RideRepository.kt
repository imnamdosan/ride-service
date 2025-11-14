package com.example.ride

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RideRepository : JpaRepository<Ride, UUID> {
    fun getRideById(id: UUID): Ride

    fun getRideByRiderId(riderId: String): List<Ride>
}