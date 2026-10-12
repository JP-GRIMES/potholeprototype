
package com.example.potholeprototype

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
@Composable
fun ReportScreen() {
    val context = LocalContext.current
    val fusedLocationClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    var locationText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severityValue by remember { mutableStateOf(2f) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var message by remember { mutableStateOf("") }

    val tags = listOf(
        "LeftSide",
        "RightSide",
        "Sidewalk",
        "Obscured"
    )

    val severity = when (severityValue.toInt()) {
        0 -> "Very Low"
        1 -> "Low"
        2 -> "Medium"
        3 -> "High"
        else -> "Very High"
    }

    val severityColor = when (severityValue.toInt()) {
        0 -> Color(0xFF2EAD59)
        1 -> Color(0xFF9BC43D)
        2 -> Color(0xFFE9B949)
        3 -> Color(0xFFEB7737)
        else -> Color(0xFFD94040)
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        locationText =
                            "${location.latitude}, ${location.longitude}"
                        message = "Current location obtained."
                    } else {
                        message = "Could not get location. Try again."
                    }
                }
            } else {
                message = "Location permission is required."
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Report a Pothole",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = locationText,
            onValueChange = {
                locationText = it
                latitude = null
                longitude = null
            },
            label = { Text("Location") },
            placeholder = { Text("Enter location or use GPS") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val fineLocationGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                val coarseLocationGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                if (fineLocationGranted || coarseLocationGranted) {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                            locationText =
                                "${location.latitude}, ${location.longitude}"
                            message = "Current location obtained."
                        } else {
                            message = "Could not get location. Try again."
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use My Location")
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Describe the pothole") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Severity: $severity",
            style = MaterialTheme.typography.titleMedium,
            color = severityColor
        )

        Slider(
            value = severityValue,
            onValueChange = { severityValue = it },
            valueRange = 0f..4f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = severityColor,
                activeTrackColor = severityColor
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Very Low",
                color = Color(0xFF2EAD59)
            )
            Text(
                text = "Very High",
                color = Color(0xFFD94040)
            )
        }

        Text(
            text = "Tags",
            style = MaterialTheme.typography.titleMedium
        )

        tags.forEach { tag ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tag in selectedTags,
                    onCheckedChange = { checked ->
                        selectedTags = if (checked) {
                            selectedTags + tag
                        } else {
                            selectedTags - tag
                        }
                    }
                )

                Text(tag)
            }
        }

        Button(
            onClick = {
                if (locationText.isBlank() || description.isBlank()) {
                    message = "Please enter a location and description."
                } else if (latitude == null || longitude == null) {
                    message = "Please use My Location to set GPS coordinates."
                } else {
                    ReportRepository.addReport(
                        location = locationText,
                        description = description,
                        severity = severity,
                        latitude = latitude!!,
                        longitude = longitude!!,
                        tags = selectedTags.toList()
                    )

                    locationText = ""
                    description = ""
                    severityValue = 2f
                    latitude = null
                    longitude = null
                    selectedTags = emptySet()
                    message = "Pothole report submitted!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Report")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
