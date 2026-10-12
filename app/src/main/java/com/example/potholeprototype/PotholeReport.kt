
package com.example.potholeprototype

data class PotholeReport(
    val id: Int,
    val location: String,
    val description: String,
    val severity: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastConfirmedAt: Long = createdAt
)
