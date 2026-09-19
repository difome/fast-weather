package com.difome.fast.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.difome.fast.R
import com.difome.fast.data.model.HourForecast
import com.difome.fast.data.model.WeatherConditionHelper
import com.difome.fast.ui.theme.MyFastTheme
import com.difome.fast.ui.theme.WeatherChartGreen
import com.difome.fast.ui.theme.WeatherSunYellow
import java.util.Calendar
import java.util.Locale

@Composable
fun HourlyForecastRow(
    hourlyList: List<HourForecast>,
    modifier: Modifier = Modifier
) {
    if (hourlyList.isNotEmpty()) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val listState = rememberLazyListState()

        val minTemp = hourlyList.minOf { it.temp }
        val maxTemp = hourlyList.maxOf { it.temp }
        val tempRange = (maxTemp - minTemp).coerceAtLeast(1)

        val currentIndex = hourlyList.indexOfFirst { it.hour == currentHour }.coerceAtLeast(0)

        LaunchedEffect(currentIndex) {
            listState.scrollToItem(currentIndex)
        }

        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(id = R.string.hourly_forecast_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(hourlyList, key = { _, item -> item.hour }) { index, item ->
                        val isNow = item.hour == currentHour
                        val timeLabel = if (isNow) {
                            stringResource(id = R.string.now)
                        } else {
                            String.format(Locale.US, "%02d:00", item.hour)
                        }

                        val prevTemp = hourlyList.getOrNull(index - 1)?.temp ?: item.temp
                        val currentTemp = item.temp
                        val nextTemp = hourlyList.getOrNull(index + 1)?.temp ?: item.temp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(52.dp)
                        ) {
                            Text(
                                text = "${item.temp}°",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Canvas(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(24.dp)
                            ) {
                                val w = size.width
                                val h = size.height

                                val yPrev = h - ((prevTemp - minTemp).toFloat() / tempRange) * (h - 8.dp.toPx()) - 4.dp.toPx()
                                val yCurr = h - ((currentTemp - minTemp).toFloat() / tempRange) * (h - 8.dp.toPx()) - 4.dp.toPx()
                                val yNext = h - ((nextTemp - minTemp).toFloat() / tempRange) * (h - 8.dp.toPx()) - 4.dp.toPx()

                                val yLeft = (yPrev + yCurr) / 2f
                                val yRight = (yCurr + yNext) / 2f

                                val linePath = Path().apply {
                                    moveTo(0f, yLeft)
                                    cubicTo(
                                        w * 0.5f, yCurr,
                                        w * 0.5f, yCurr,
                                        w, yRight
                                    )
                                }

                                drawPath(
                                    path = linePath,
                                    color = WeatherChartGreen,
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                )

                                if (isNow) {
                                    drawCircle(
                                        color = WeatherChartGreen,
                                        radius = 5.5.dp.toPx(),
                                        center = Offset(w / 2f, yCurr)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4.dp.toPx(),
                                        center = Offset(w / 2f, yCurr)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Icon(
                                imageVector = WeatherConditionHelper.getIcon(item.conditionCode, item.hour),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = WeatherSunYellow
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = timeLabel,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal,
                                color = if (isNow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "24 Hours Forecast Preview")
@Composable
fun HourlyForecastRowPreview() {
    MyFastTheme {
        HourlyForecastRow(
            hourlyList = listOf(
                HourForecast(0, 16, 0),
                HourForecast(3, 15, 0),
                HourForecast(6, 14, 0),
                HourForecast(9, 18, 100),
                HourForecast(12, 24, 0),
                HourForecast(15, 27, 0),
                HourForecast(18, 23, 200),
                HourForecast(21, 19, 400)
            )
        )
    }
}
