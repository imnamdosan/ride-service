package com.example.ride

import java.time.OffsetDateTime

// The custom body in an error response returned to the client
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?,
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val description: Map<String, String>? = null
)