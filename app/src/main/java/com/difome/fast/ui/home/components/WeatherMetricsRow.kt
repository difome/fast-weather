package com.difome.fast.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.difome.fast.R
import com.difome.fast.common.UnitConverter
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.ui.theme.MyFastTheme
import java.util.Locale

@Composable
fun WeatherMetricsRow(
    weather: WeatherUI,
    windUnit: String = "ms",
    modifier: Modifier = Modifier
) {
    val displayWind = if (windUnit == "kmh") UnitConverter.toKmH(weather.windSpeed) else weather.windSpeed
    val windUnitText = if (windUnit == "kmh") stringResource(id = R.string.unit_kmh) else stringResource(id = R.string.unit_ms)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.humidity),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weather.humidity}%",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.wind),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Locale.US, "%.1f %s", displayWind, windUnitText),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Weather Metrics Row Preview")
@Composable
fun WeatherMetricsRowPreview() {
    MyFastTheme {
        WeatherMetricsRow(
            weather = WeatherUI(
                city = "Krasnodar",
                temp = 24,
                feelsLike = 25,
                minTemp = 18,
                maxTemp = 28,
                humidity = 65,
                windSpeed = 3.5
            )
        )
    }
}
