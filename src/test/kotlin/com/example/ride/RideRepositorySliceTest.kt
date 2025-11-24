package com.example.ride

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

@Testcontainers // Starts the containers marked with @Container
@DataJpaTest // Constructs the persistence layer of the application (DB + entities + repos)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Don't start in-memory DB, use Postgres
class RideRepositorySliceTest @Autowired constructor(
    private val rideRepository: RideRepository
){
    // Singleton which exposes the postgres property
    companion object {
        @Container // Starts/stops this container before/after test
        @ServiceConnection // Container is a service, create connection details (JDBC URL/username/pw)
        // and uses these credentials to create the DataSource (connection pool which holds connection with DB)
        @JvmStatic // By default, properties in objects aren't static, they're accessed via getter method
        val postgres = PostgreSQLContainer("postgres:16")
    }
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