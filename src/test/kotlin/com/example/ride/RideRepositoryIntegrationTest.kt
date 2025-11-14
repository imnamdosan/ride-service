package com.example.ride

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*
import kotlin.test.assertEquals

@DataJpaTest
class RideRepositoryIntegrationTest @Autowired constructor(
    private val rideRepository: RideRepository
){
    @Test
    fun `save then findById returns the same entity`() {
        // Given
        val ride = Ride(riderId = "ABC123", pickup = "11 Hood Street", destination = "Melb CBD")
        val id = ride.id

        // When
        rideRepository.save(ride)
        val found = rideRepository.findById(id).orElseThrow()

        // Then
        assertEquals(found.id, id)
        assertEquals(found.status, RideStatus.PENDING)
    }
}