
package com.example.potholeprototype

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeedScreen() {
    val reports = ReportRepository.reports

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Nearby Potholes",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(
            items = reports,
            key = { report -> report.id }
        ) { report ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = report.location,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Severity: ${report.severity}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (report.tags.isNotEmpty()) {
                        Text(
                            text = "Tags: ${report.tags.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Date Created",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = formatDate(report.createdAt),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.padding(4.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Last Confirmed",
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = formatDate(report.lastConfirmedAt),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Button(
                        onClick = {
                            ReportRepository.confirmReport(report.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Still There")
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "MMM d, yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}
