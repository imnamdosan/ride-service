package com.example.ride

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals

@WebMvcTest
@TestPropertySource(properties = ["spring.data.web.pageable.max-page-size=20"])
class RideControllerWebTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var springDataWebProperties: SpringDataWebProperties
    @MockkBean lateinit var rideService: RideService

    @Test
    fun `POST rides returns 201 with Location and body`() {
        val id = UUID.randomUUID()
        val riderId = "ABC123"
        val pickup = "11 Hood Street"
        val destination = "Melbourne CBD"
        val ride = Ride(id, riderId, pickup, destination)

        every { rideService.createRide(any()) } returns ride

        mockMvc.post("/rides/request") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "riderId": "$riderId",
                    "pickup": "$pickup",
                    "destination": "$destination"
                }
            """.trimIndent()
        }.andExpect {
            status { isCreated() }
            header { string("Location", "/rides/$id") }
            content { ContentType.APPLICATION_JSON }
            jsonPath("$.riderId")       { value(riderId) }
            jsonPath("$.pickup")        { value(pickup) }
            jsonPath("$.destination")   { value(destination) }
            jsonPath("$.status")        { value(RideStatus.PENDING.name) }
        }
    }

    @Test
    fun `POST rides returns 400 if missing pickup in request`() {
        val riderId = "ABC123"
        val destination = "Melbourne CBD"

        mockMvc.post("/rides/request") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "riderId": "$riderId",
                    "destination": "$destination"
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            content { ContentType.APPLICATION_JSON }
            jsonPath("$.status") { value(HttpStatus.BAD_REQUEST.value()) }
            jsonPath("$.error") { value(HttpStatus.BAD_REQUEST.reasonPhrase) }
            jsonPath("$.message") { value("Invalid request body") }
            jsonPath("$.path") { value("/rides/request")}
        }
    }

    @Test
    fun `POST rides returns 400 if pickup same as destination`() {
        val riderId = "ABC123"
        val pickup = "Melbourne CBD"
        val destination = "Melbourne CBD"
        val endpoint = "/rides/request"
        every { rideService.createRide(any())} throws BadRequestException("Pickup and destination addresses are the same")

        mockMvc.post(endpoint) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "riderId": "$riderId",
                    "pickup": "$pickup", 
                    "destination": "$destination"
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            content { ContentType.APPLICATION_JSON }
            jsonPath( "$.status") { value(HttpStatus.BAD_REQUEST.value()) }
            jsonPath( "$.message") { value("Pickup and destination addresses are the same") }
            jsonPath("$.path") { value(endpoint) }

        }
    }

    @Test
    fun `POST rides with empty fields returns 400 and validation errors`() {
        val endpoint = "/rides/request"

        mockMvc.post(endpoint) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "riderId": "",
                    "pickup": "", 
                    "destination": ""
                }
            """.trimIndent()
        }.andExpect {
            status { isBadRequest() }
            content { ContentType.APPLICATION_JSON }
            jsonPath("$.description['riderId']")     { exists() }
            jsonPath("$.description['pickup']")      { exists() }
            jsonPath("$.description['destination']") { exists() }
        }
    }

    @Test
    fun `GET ride returns 200 with body`() {
        val id = UUID.randomUUID()
        val riderId = "ABC123"
        val pickup = "11 Hood Street"
        val destination = "Melbourne CBD"
        val ride = Ride(id, riderId, pickup, destination)

        every { rideService.getRide(any()) } returns ride

        mockMvc.get("/rides/$id")
            .andExpect {
                status{ isOk() }
                content { ContentType.APPLICATION_JSON }
                jsonPath("$.riderId")     { value(riderId) }
                jsonPath("$.pickup")      { value(pickup) }
                jsonPath("$.destination") { value(destination) }
            }
    }

    @Test
    fun `GET ride returns 400 for invalid UUID`() {
        val uuid = "ABC123"

        mockMvc.get("/rides/$uuid")
            .andExpect {
                status { isBadRequest() }
                content { ContentType.APPLICATION_JSON }
            }
    }

    @Test
    fun `GET ride returns 404 for nonexistent ride`() {
        val uuid = UUID.randomUUID()

        every { rideService.getRide(any()) } throws NotFoundException("Ride $uuid not found")

        mockMvc.get("/rides/$uuid")
            .andExpect {
                status{ isNotFound() }
                content { ContentType.APPLICATION_JSON }
                jsonPath("$.message") { value("Ride $uuid not found") }
                jsonPath("$.path") { value("/rides/$uuid")}
            }
    }

    @Test
    fun `GET listRides returns page of specified size and sort`() {
        val rides = listOf(
            Ride(
                UUID.randomUUID(),
                "ABC123",
                "11 Hood St",
                "Melbourne CBD",
                RideStatus.PENDING,
                OffsetDateTime.now()
            ),
            Ride(
                UUID.randomUUID(),
                "ABC124",
                "13 Hood St",
                "Bun Bun Bakery",
                RideStatus.PENDING,
                OffsetDateTime.now().minusMinutes(10)
            )
        )
        val pageRequest = PageRequest.of(
            2,
            5,
            Sort.by("createdAt").descending()
        )
        val totalRidesInDb = 12L
        val page: Page<Ride> = PageImpl(
            rides,
            pageRequest,
            totalRidesInDb
        )
        val pageableSlot = slot<Pageable>()
        every { rideService.listRides(capture(pageableSlot))} returns page

        mockMvc.get("/rides") {
            param("page", pageRequest.pageNumber.toString())
            param("size", pageRequest.pageSize.toString())
            param("sort", "createdAt,desc")
        }
            .andExpect {
                status{ isOk() }
                content { ContentType.APPLICATION_JSON }

                // verify page content
                jsonPath("$.content.length()") { value(rides.size)}
                for (i in 0 until rides.size) {
                    jsonPath("$.content[$i].riderId") { value(rides[i].riderId) }
                }

                // verify page metadata
                val totalPages = (totalRidesInDb + pageRequest.pageSize - 1) / pageRequest.pageSize
                jsonPath("$.totalPages")    { value(totalPages) }
                jsonPath("$.number")        { value(pageRequest.pageNumber) }
                jsonPath("$.size")          { value(pageRequest.pageSize)}
                jsonPath("$.totalElements") { value(totalRidesInDb) }
            }

        // verify the page request passed to the service
        // Spring's object construction may be different, so can't do assertEquals(captured, pageRequest)
        val captured = pageableSlot.captured
        assertEquals(captured.pageNumber, pageRequest.pageNumber)
        assertEquals(captured.pageSize, pageRequest.pageSize)
        assertEquals(captured.sort.first().property, "createdAt")
        assertEquals(captured.sort.first().direction, Sort.Direction.DESC)
    }

    @Test
    fun `GET listRides limits maximum page size`() {
        val rides = listOf(
            Ride(
                UUID.randomUUID(),
                "ABC123",
                "11 Hood St",
                "Melbourne CBD",
                RideStatus.PENDING,
                OffsetDateTime.now()
            ),
            Ride(
                UUID.randomUUID(),
                "ABC124",
                "13 Hood St",
                "Bun Bun Bakery",
                RideStatus.PENDING,
                OffsetDateTime.now()
            )
        )
        val pageRequest = PageRequest.of(0, 1_000_000,)
        val totalRidesInDb = 1_000_000L

        val maxPageSize = springDataWebProperties.pageable.maxPageSize

        val pageableSlot = slot<Pageable>()
        every { rideService.listRides(capture(pageableSlot))} answers {
            val actualPageRequest = firstArg<Pageable>()
            PageImpl(rides, actualPageRequest, totalRidesInDb)
        }

        mockMvc.get("/rides") {
            param("page", pageRequest.pageNumber.toString())
            param("size", pageRequest.pageSize.toString())
        }
            .andExpect {
                status{ isOk() }
                content { ContentType.APPLICATION_JSON }
                jsonPath("$.content.length()") { value(rides.size) }
                jsonPath("$.size")             { value(maxPageSize) }
                jsonPath("$.number")       { value(pageRequest.pageNumber) }
                jsonPath("$.totalElements")    { value(totalRidesInDb) }
            }

        val captured = pageableSlot.captured
        assertEquals(captured.pageNumber, pageRequest.pageNumber)
        // Spring limits the max page size when building the PageRequest after parsing the request args
        assertEquals(captured.pageSize, maxPageSize)
    }
}