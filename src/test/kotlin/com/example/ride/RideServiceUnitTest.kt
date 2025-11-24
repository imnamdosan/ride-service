package com.example.ride

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RideServiceUnitTest {
    private val rideRepository: RideRepository = mockk<RideRepository>()
    private val rideService: RideService = RideService(rideRepository)

    @Test
    fun `createRide trims fields and saves`() {
        val req = CreateRideRequestDTO(
            "   ABC123   ",
            "    11 Hood Street   ",
            "    Melbourne CBD  "
        )
        every { rideRepository.save(any()) } answers { firstArg() }

        val ride = rideService.createRide(req)

        assertEquals(ride.riderId, "ABC123")
        assertEquals(ride.pickup, "11 Hood Street")
        assertEquals(ride.destination, "Melbourne CBD")
    }

    @Test
    fun `createRide throws when pickup equals destination ignoring case and space`() {
        val req = CreateRideRequestDTO(
            "   ABC123   ",
            "    11 Hood Street   ",
            "    11 HoOd StrEet  "
        )
        assertThrows<BadRequestException> {
            rideService.createRide(req)
        }
    }

    @Test
    fun `getRide throws when id does not exist`() {
        val id = UUID.randomUUID()
        every { rideRepository.findById(id) } answers { Optional.empty() }
        assertThrows<NotFoundException> {
            rideService.getRide(id)
        }
    }
}