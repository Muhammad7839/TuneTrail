package com.example.tunetrail

import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.io.File

/**
 * A Composable screen that displays a progress report for a specific child.
 * It reads attempt data from a CSV file, calculates success rates per level,
 * and displays the data in a bar chart and as text. It also provides an option
 * to share a summary of the progress.
 *
 * @param kidId The ID of the child for whom to display the report.
 */
@Composable
fun ParentReportScreen(kidId: Long) {
    // Get the current context, needed for file access and starting activities.
    val ctx = LocalContext.current

    // State variables to hold the calculated success rates, data availability, and any errors.
    var successRates by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var hasData by remember { mutableStateOf<Boolean?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(kidId) {
        try {
            val logFile = File(ctx.filesDir, "attempts.csv")

            // If the log file doesn't exist, there's no data to process.
            if (!logFile.exists()) {
                hasData = false
                successRates = emptyMap()
                errorText = null
                return@LaunchedEffect
            }

            // Maps to count successes and total attempts for each level.
            val successCounts = mutableMapOf<Int, Int>()
            val totalCounts = mutableMapOf<Int, Int>()

            // Read the CSV file line by line.
            logFile.forEachLine { line ->
                // timestamp,kidId,level,game,success,moves,timeMs
                val parts = line.split(",")
                if (parts.size >= 7) {
                    val level = parts[2].toIntOrNull()
                    val successRaw = parts[4].trim()
                    val success = successRaw.equals("true", ignoreCase = true)

                    // Aggregate counts for each level.
                    if (level != null) {
                        totalCounts[level] = (totalCounts[level] ?: 0) + 1
                        if (success) {
                            successCounts[level] = (successCounts[level] ?: 0) + 1
                        }
                    }
                }
            }

            // Calculate the success rate for each level.
            val rates = mutableMapOf<Int, Float>()
            for ((level, total) in totalCounts) {
                val ok = successCounts[level] ?: 0
                if (total > 0) {
                    rates[level] = ok.toFloat() / total.toFloat()
                }
            }

            // Update the state with the calculated rates.
            successRates = rates
            hasData = rates.isNotEmpty()
            if (hasData == false) {
                errorText = null
            }
        } catch (e: Throwable) {
            // Handle any exceptions during file reading or processing.
            hasData = false
            successRates = emptyMap()
            errorText = "Could not load progress (${e::class.simpleName})"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Report for Kid #$kidId")
        Spacer(Modifier.height(12.dp))

        errorText?.let {
            Text(it)
            Spacer(Modifier.height(12.dp))
        }

        // Display UI based on the data loading state.
        when (hasData) {
            null -> Text("Loading progress...")
            false -> {
                if (errorText == null) {
                    Text("No progress recorded yet.")
                }
            }
            true -> {
                Text("Success Rate by Level:")
                Spacer(Modifier.height(12.dp))

                // Embed the MPAndroidChart BarChart using AndroidView.
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    factory = { context ->
                        BarChart(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                // Set layout parameters for the chart view.
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            axisRight.isEnabled = false
                            axisLeft.axisMinimum = 0f
                            axisLeft.axisMaximum = 1f
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.granularity = 1f
                            xAxis.setDrawGridLines(false)
                            description = Description().apply { text = "" }
                            setFitBars(true)
                        }
                    },
                    update = { chart ->
                        // This block is called when `successRates` state changes.
                        val entries = successRates.entries
                            .sortedBy { it.key }
                            .map { (level, rate) ->
                                BarEntry(level.toFloat(), rate)
                            }

                        val dataSet = BarDataSet(entries, "Success Rate by Level").apply {
                            valueTextColor = Color.BLACK
                            valueTextSize = 12f
                        }

                        chart.data = BarData(dataSet).apply { barWidth = 0.6f }
                        chart.invalidate()
                    }
                )

                Spacer(Modifier.height(16.dp))
                // Display the success rates as text below the chart.
                successRates.entries
                    .sortedBy { it.key }
                    .forEach { (level, rate) ->
                        Text("Level $level: ${(rate * 100).toInt()}% success")
                    }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Button to export/share the progress report.
        Button(
            onClick = {
                // Simple text summary for sharing
                val summary = buildString {
                    append("TuneTrail progress for Kid #$kidId\n")
                    if (successRates.isEmpty()) {
                        append("No progress recorded yet.\n")
                    } else {
                        successRates.entries.sortedBy { it.key }.forEach { (level, rate) ->
                            val percent = (rate * 100).toInt()
                            append("Level $level: $percent% success\n")
                        }
                    }
                }

                // Create a share Intent to send the text summary.
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "TuneTrail Progress Report")
                    putExtra(Intent.EXTRA_TEXT, summary)
                }

                ctx.startActivity(Intent.createChooser(sendIntent, "Share progress via"))
            }
        ) {
            Text("Export Progress")
        }
    }
}