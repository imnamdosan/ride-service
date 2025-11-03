package com.example.ride

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RideServiceApplication

fun main(args: Array<String>) {
    runApplication<RideServiceApplication>(*args)
}