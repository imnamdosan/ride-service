package com.example.ride

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateRideRequestDTO(
    @field:NotBlank
    @field:Size(max = 64)
    val riderId: String,

    @field:NotBlank
    @field:Size(max = 200)
    val pickup: String,

    @field:NotBlank
    @field:Size(max = 200)
    val destination: String
)