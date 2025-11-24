package com.example.ride

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RideService(
    private val rideRepository: RideRepository
) {
    companion object {
        private val log = LoggerFactory.getLogger(RideService::class.java)
    }

    @Transactional
    fun createRide(req: CreateRideRequestDTO): Ride {
        log.debug("Creating ride for rider Id={}, pickup={}, destination={}",
            req.riderId, req.pickup, req.destination)

        val ride = RideMapper.toEntity(req)
        return rideRepository.save(ride)
    }

    // Tells Hibernate to disable dirty checking (since reads don't dirty entities)
    // Doesn't need to mark this transaction for commit
    @Transactional(readOnly = true)
    fun getRide(id: UUID): Ride {
        return rideRepository.findById(id).orElseThrow{
            log.warn("Ride not found id={}", id)
            NotFoundException("Ride $id not found")
        }
    }

    @Transactional(readOnly = true)
    fun listRides(pageable: Pageable): Page<Ride> {
        return rideRepository.findAll(pageable)
    }


    // TODO: Add a new Rider entity and repository
//    fun getRidesByRider(riderId: String): List<Ride> {
//        if (!riderRepository.getById(riderId)) {
//            throw RiderNotFoundException(riderId)
//        }
//        return rideRepository.getRideByRiderId(riderId)
//    }
}