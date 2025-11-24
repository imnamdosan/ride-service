package com.example.ride

// Turns a request DTO into a Ride object
object RideMapper {
    fun toEntity(req: CreateRideRequestDTO): Ride {
        val ride = Ride(
            riderId = req.riderId.trim(),
            pickup = req.pickup.trim(),
            destination = req.destination.trim()
        )
        if (ride.pickup.equals(ride.destination, ignoreCase = true)) {
            throw BadRequestException("Pickup and destination addresses are the same")
        }
        return ride
    }

    fun toResponse(ride: Ride): RideResponseDTO {
        return RideResponseDTO(
            ride.id,
            ride.riderId,
            ride.pickup,
            ride.destination,
            ride.status,
            ride.createdAt
        )
    }
}