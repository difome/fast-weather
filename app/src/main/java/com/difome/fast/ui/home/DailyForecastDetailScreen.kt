package com.difome.fast.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.difome.fast.common.AppConstants
import com.difome.fast.common.UnitConverter
import com.difome.fast.data.model.DayForecast
import com.difome.fast.data.model.WeatherConditionHelper
import com.difome.fast.ui.theme.MyFastTheme
import com.difome.fast.ui.theme.WeatherNightPurple
import com.difome.fast.ui.theme.WeatherRainBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyForecastDetailScreen(
    dailyList: List<DayForecast>,
    modifier: Modifier = Modifier,
    isFahrenheit: Boolean = false,
    windUnit: String = AppConstants.WIND_UNIT_MS,
    onBackClick: () -> Unit = {}
) {
    val unitKmh = stringResource(id = R.string.unit_kmh)
    val unitMs = stringResource(id = R.string.unit_ms)

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.view_full_forecast),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (convertedList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.loading))
            }
            return@Scaffold
        }

        val allMax = convertedList.maxOf { it.maxTemp }
        val allMin = convertedList.minOf { it.minTemp }
        val range = (allMax - allMin).coerceAtLeast(1)

        val listState = rememberLazyListState()
        val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                state = listState,
                flingBehavior = snapFlingBehavior,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                items(count = convertedList.size) { index ->
                    val day = convertedList[index]
                    val isToday = index == 0
                    val prevDay = convertedList.getOrNull(index - 1) ?: day
                    val nextDay = convertedList.getOrNull(index + 1) ?: day

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isToday) MaterialTheme.colorScheme.surfaceVariant
                                else Color.Transparent
                            )
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            imageVector = WeatherConditionHelper.getIcon(day.conditionCode, isNight = false),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${day.maxTemp}°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val outlineVariant = MaterialTheme.colorScheme.outlineVariant
                        val pathMax = remember { Path() }
                        val pathMin = remember { Path() }

                        Canvas(
                            modifier = Modifier
                                .width(80.dp)
                                .height(120.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val yMaxCurr = h * 0.35f - ((day.maxTemp - allMin).toFloat() / range) * (h * 0.3f)
                            val yMaxPrev = h * 0.35f - ((prevDay.maxTemp - allMin).toFloat() / range) * (h * 0.3f)
                            val yMaxNext = h * 0.35f - ((nextDay.maxTemp - allMin).toFloat() / range) * (h * 0.3f)

                            val yMinCurr = h * 0.85f - ((day.minTemp - allMin).toFloat() / range) * (h * 0.3f)
                            val yMinPrev = h * 0.85f - ((prevDay.minTemp - allMin).toFloat() / range) * (h * 0.3f)
                            val yMinNext = h * 0.85f - ((nextDay.minTemp - allMin).toFloat() / range) * (h * 0.3f)

                            pathMax.reset()
                            pathMax.moveTo(0f, (yMaxPrev + yMaxCurr) / 2f)
                            pathMax.cubicTo(w * 0.5f, yMaxCurr, w * 0.5f, yMaxCurr, w, (yMaxCurr + yMaxNext) / 2f)

                            pathMin.reset()
                            pathMin.moveTo(0f, (yMinPrev + yMinCurr) / 2f)
                            pathMin.cubicTo(w * 0.5f, yMinCurr, w * 0.5f, yMinCurr, w, (yMinCurr + yMinNext) / 2f)

                            drawPath(
                                path = pathMax,
                                color = outlineVariant,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(w / 2f, yMaxCurr))

                            drawPath(
                                path = pathMin,
                                color = outlineVariant.copy(alpha = 0.6f),
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawCircle(color = Color.LightGray, radius = 4.dp.toPx(), center = Offset(w / 2f, yMinCurr))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${day.minTemp}°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = WeatherConditionHelper.getIcon(day.nightConditionCode, isNight = true),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val windSuffix = if (windUnit == AppConstants.WIND_UNIT_KMH) unitKmh else unitMs
                        val windVal = if (windUnit == AppConstants.WIND_UNIT_KMH) UnitConverter.toKmH(day.windSpeed).toInt().toString() else day.windSpeed.toString()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(11.dp)
                                    .rotate(UnitConverter.windDirectionToDegrees(day.windDirection)),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$windVal $windSuffix",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
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
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Daily Forecast Detail Screen Preview", heightDp = 700)
@Composable
fun DailyForecastDetailScreenPreview() {
    MyFastTheme {
        DailyForecastDetailScreen(
            dailyList = listOf(
                DayForecast(
                    dateString = "2026-09-20",
                    dayOfWeek = "Нд",
                    formattedDate = "20.09",
                    minTemp = 18,
                    maxTemp = 27,
                    conditionCode = 200,
                    nightConditionCode = 0,
                    windSpeed = 2.4,
                    humidity = 60,
                    cloudiness = 20,
                    precipProbability = 0,
                    windDirection = "NE"
                ),
                DayForecast(
                    dateString = "2026-09-21",
                    dayOfWeek = "Пн",
                    formattedDate = "21.09",
                    minTemp = 16,
                    maxTemp = 26,
                    conditionCode = 310,
                    nightConditionCode = 100,
                    windSpeed = 2.4,
                    humidity = 70,
                    cloudiness = 76,
                    precipProbability = 81,
                    windDirection = "S"
                ),
                DayForecast(
                    dateString = "2026-09-22",
                    dayOfWeek = "Вт",
                    formattedDate = "22.09",
                    minTemp = 18,
                    maxTemp = 26,
                    conditionCode = 210,
                    nightConditionCode = 100,
                    windSpeed = 2.8,
                    humidity = 65,
                    cloudiness = 49,
                    precipProbability = 57,
                    windDirection = "SW"
                ),
                DayForecast(
                    dateString = "2026-09-23",
                    dayOfWeek = "Ср",
                    formattedDate = "23.09",
                    minTemp = 17,
                    maxTemp = 25,
                    conditionCode = 410,
                    nightConditionCode = 400,
                    windSpeed = 3.2,
                    humidity = 80,
                    cloudiness = 90,
                    precipProbability = 99,
                    windDirection = "E"
                )
            )
        )
    }
}
