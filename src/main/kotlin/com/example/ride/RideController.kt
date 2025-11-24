package com.example.ride

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/rides")
class RideController(private val rideService: RideService) {
    @GetMapping(
        path = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRide(@PathVariable("id") rideId: UUID): RideResponseDTO {
        val ride = rideService.getRide(rideId)
        val dto = RideMapper.toResponse(ride)
        return dto
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listRides(
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): Page<RideResponseDTO> {
        val rides = rideService.listRides(pageable)
        return rides.map{ RideMapper.toResponse(it) }
    }

    @PostMapping(
        path = ["/request"],
        // Means that the endpoint expects JSON format in the body from the client
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createRide(@Valid @RequestBody dto: CreateRideRequestDTO): ResponseEntity<RideResponseDTO> {
        val ride = rideService.createRide(dto)
        val responseDto = RideMapper.toResponse(ride)
        return ResponseEntity
            .created(URI.create("/rides/${ride.id}"))
            .body(responseDto)
    }
}