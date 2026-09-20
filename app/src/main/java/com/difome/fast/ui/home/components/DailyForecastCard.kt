package com.difome.fast.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.difome.fast.R
import com.difome.fast.common.UnitConverter
import com.difome.fast.data.model.DayForecast
import com.difome.fast.data.model.WeatherConditionHelper
import com.difome.fast.ui.theme.WeatherRainBlue

@Composable
fun DailyForecastCard(
    dailyList: List<DayForecast>,
    modifier: Modifier = Modifier,
    isFahrenheit: Boolean = false,
    onMoreClick: () -> Unit = {}
) {
    if (dailyList.isEmpty()) return

    val convertedList = remember(dailyList, isFahrenheit) {
        if (isFahrenheit) {
            dailyList.map {
                it.copy(
                    minTemp = UnitConverter.toFahrenheit(it.minTemp),
                    maxTemp = UnitConverter.toFahrenheit(it.maxTemp)
                )
            }
        } else {
            dailyList
        }
    }

    val displayList = convertedList.take(7)
    val overallMin = displayList.minOf { it.minTemp }
    val overallMax = displayList.maxOf { it.maxTemp }
    val overallRange = (overallMax - overallMin).coerceAtLeast(1)

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.five_day_forecast_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(id = R.string.more_details),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onMoreClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            displayList.forEachIndexed { index, day ->
                val minT = if (isFahrenheit) UnitConverter.toFahrenheit(day.minTemp) else day.minTemp
                val maxT = if (isFahrenheit) UnitConverter.toFahrenheit(day.maxTemp) else day.maxTemp

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.width(44.dp)) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Umbrella,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (day.precipProbability > 0) WeatherRainBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "${day.precipProbability}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (day.precipProbability > 0) WeatherRainBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontWeight = if (day.precipProbability > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Icon(
                        imageVector = WeatherConditionHelper.getIcon(day.conditionCode),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = Color.Unspecified
                    )

                    Text(
                        text = "${minT}°",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )

                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        val startFrac = ((minT - overallMin).toFloat() / overallRange).coerceIn(0f, 1f)
                        val endFrac = ((maxT - overallMin).toFloat() / overallRange).coerceIn(0f, 1f)

                        val barStart = startFrac * w
                        val barWidth = ((endFrac - startFrac) * w).coerceAtLeast(8.dp.toPx())

                        drawRoundRect(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(h / 2, h / 2)
                        )

                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF38BDF8), Color(0xFFFACC15), Color(0xFFF97316))
                            ),
                            topLeft = Offset(barStart, 0f),
                            size = Size(barWidth, h),
                            cornerRadius = CornerRadius(h / 2, h / 2)
                        )

                        val indicatorX = barStart + barWidth / 2f
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(indicatorX, h / 2f)
                        )
                    }

                    Text(
                        text = "${maxT}°",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.width(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            modifier = Modifier
                                .size(12.dp)
                                .rotate(UnitConverter.windDirectionToDegrees(day.windDirection)),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${day.windSpeed.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (index < displayList.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onMoreClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = stringResource(id = R.string.view_full_forecast),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
