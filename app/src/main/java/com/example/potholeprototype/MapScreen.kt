
package com.example.potholeprototype

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Suppress("DEPRECATION")
@Composable
fun MapScreen() {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue =
        "PotholePrototype/1.0 (Android)"

    val reports by remember {
        snapshotFlow { ReportRepository.reports.toList() }
    }.collectAsState(initial = ReportRepository.reports.toList())

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
            controller.setCenter(GeoPoint(33.9985, -81.0284))
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            map.overlays.removeAll { it is Marker }

            reports.forEach { report ->
                val marker = Marker(map)

                marker.position = GeoPoint(
                    report.latitude,
                    report.longitude
                )
                marker.title = report.location
                marker.snippet =
                    "Severity: ${report.severity}\n${report.description}"

                val markerColor = when (report.severity.lowercase()) {
                    "very low" -> Color.rgb(46, 173, 89)
                    "low" -> Color.rgb(155, 196, 61)
                    "medium" -> Color.rgb(233, 185, 73)
                    "high" -> Color.rgb(235, 119, 55)
                    "very high" -> Color.rgb(217, 64, 64)
                    else -> Color.GRAY
                }

                val icon = AppCompatResources.getDrawable(
                    context,
                    org.osmdroid.library.R.drawable.marker_default
                )

                if (icon != null) {
                    val coloredIcon = icon.mutate()
                    coloredIcon.setColorFilter(
                        markerColor,
                        PorterDuff.Mode.SRC_IN
                    )
                    marker.icon = coloredIcon
                }

                marker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                map.overlays.add(marker)
            }

            map.invalidate()
        }
    )
}
