
package com.example.potholeprototype

import androidx.compose.runtime.mutableStateListOf

object ReportRepository {
    val reports = mutableStateListOf(
        PotholeReport(
            id = 1,
            location = "Near Thomas Cooper Library",
            description = "Large pothole near the sidewalk entrance.",
            severity = "High",
            latitude = 33.9985,
            longitude = -81.0284,
            tags = listOf("Sidewalk")
        ),
        PotholeReport(
            id = 2,
            location = "Near Russell House",
            description = "Small pothole beside the road.",
            severity = "Low",
            latitude = 33.9992,
            longitude = -81.0268
        ),
        PotholeReport(
            id = 3,
            location = "Near the Horseshoe",
            description = "Pothole partially hidden by standing water.",
            severity = "Medium",
            latitude = 34.0007,
            longitude = -81.0272,
            tags = listOf("Obscured")
        )
    )

    private var nextId = 4

    fun addReport(
        location: String,
        description: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        tags: List<String> = emptyList()
    ) {
        reports.add(
            0,
            PotholeReport(
                id = nextId++,
                location = location,
                description = description,
                severity = severity,
                latitude = latitude,
                longitude = longitude,
                tags = tags
            )
        )
    }

    fun confirmReport(id: Int) {
        val index = reports.indexOfFirst { it.id == id }

        if (index != -1) {
            reports[index] = reports[index].copy(
                lastConfirmedAt = System.currentTimeMillis()
            )
        }
    }
}
