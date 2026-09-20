package com.difome.fast.ui.home.components

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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.difome.fast.R
import com.difome.fast.common.AppConstants
import com.difome.fast.common.UnitConverter
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.ui.theme.MyFastTheme
import com.difome.fast.ui.theme.WeatherNightPurple
import com.difome.fast.ui.theme.WeatherSunYellow
import java.util.Locale

@Composable
fun WeatherMetricsRow(
    modifier: Modifier = Modifier,
    weather: WeatherUI,
    windUnit: String = AppConstants.WIND_UNIT_MS,
    pressureUnit: String = AppConstants.PRESSURE_UNIT_MBAR
) {
    val displayWind = if (windUnit == AppConstants.WIND_UNIT_KMH) UnitConverter.toKmH(weather.windSpeed) else weather.windSpeed
    val windUnitText = if (windUnit == AppConstants.WIND_UNIT_KMH) stringResource(id = R.string.unit_kmh) else stringResource(id = R.string.unit_ms)

    val isMmHg = pressureUnit == AppConstants.PRESSURE_UNIT_MMHG
    val displayPressure = if (isMmHg) UnitConverter.toMmHg(weather.pressure) else UnitConverter.toMbar(weather.pressure)
    val pressureUnitText = if (isMmHg) stringResource(id = R.string.unit_mmhg) else stringResource(id = R.string.unit_mbar)

    val unitMm = stringResource(id = R.string.unit_mm)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.humidity),
                value = "${weather.humidity}%",
                icon = Icons.Default.WaterDrop
            )

            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(id = R.string.wind),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (weather.windDirection.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(UnitConverter.windDirectionToDegrees(weather.windDirection)),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        val windValStr = String.format(Locale.US, "%.1f %s", displayWind, windUnitText)
                        Text(
                            text = windValStr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val precipVal = if (weather.precipAmount > 0) {
                "${weather.precipProbability}% (${weather.precipAmount} $unitMm)"
            } else {
                "${weather.precipProbability}%"
            }

            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.precipitation),
                value = precipVal,
                icon = Icons.Outlined.Umbrella
            )

            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.pressure),
                value = "$displayPressure $pressureUnitText",
                icon = Icons.Default.Compress
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val riseStr = weather.sunrise.ifEmpty { "06:00" }
            val setStr = weather.sunset.ifEmpty { "18:00" }

            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(id = R.string.sun_cycle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = WeatherSunYellow
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = riseStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = WeatherNightPurple
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = setStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.cloudiness),
                value = "${weather.cloudiness}%",
                icon = Icons.Default.Cloud
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Weather Metrics Row Preview")
@Composable
fun WeatherMetricsRowPreview() {
    MyFastTheme {
        WeatherMetricsRow(
            weather = WeatherUI(
                city = "Kyiv",
                temp = 24,
                feelsLike = 25,
                minTemp = 18,
                maxTemp = 28,
                humidity = 65,
                windSpeed = 3.5,
                pressure = 101900,
                cloudiness = 76,
                precipProbability = 81,
                precipAmount = 0.3,
                windDirection = "NE",
                sunrise = "06:08",
                sunset = "18:25"
            )
        )
    }
}
