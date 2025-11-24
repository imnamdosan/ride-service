package com.example.ride

import java.time.OffsetDateTime
import java.util.*

data class RideResponseDTO(
    val id: UUID,
    val riderId: String,
    val pickup: String,
    val destination: String,
    val status: RideStatus,
    val createdAt: OffsetDateTime
)