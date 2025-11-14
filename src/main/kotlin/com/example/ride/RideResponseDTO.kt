package com.example.ride

data class RideResponseDTO(
    val riderId: String,
    val pickup: String,
    val destination: String,
    val status: RideStatus
) {
    companion object {
        fun from(ride: Ride): RideResponseDTO {
            return RideResponseDTO(
                ride.riderId,
                ride.pickup,
                ride.destination,
                ride.status
            )
        }
    }
}