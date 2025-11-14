package com.example.ride

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "rides")
class Ride(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "rider_id", nullable = false, length = 64)
    val riderId: String,

    @Column(name = "pickup", nullable = false, columnDefinition = "text")
    var pickup: String,

    @Column(name = "destination", nullable = false, columnDefinition = "text")
    var destination: String,

    @Column(name = "status", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    var status: RideStatus = RideStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),

    @Version
    var version: Int = 0
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = OffsetDateTime.now()
    }

    @PrePersist
    fun onCreate() {
        val now = OffsetDateTime.now()
        createdAt = now
        updatedAt = now
    }
}