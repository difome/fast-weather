package com.difome.fast.ui.home.components

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.difome.fast.R
import com.difome.fast.common.UnitConverter
import com.difome.fast.data.model.WeatherConditionHelper
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.ui.theme.MyFastTheme

@Composable
fun WeatherHeroCard(
    weather: WeatherUI,
    isFahrenheit: Boolean = false,
    onCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTemp = if (isFahrenheit) UnitConverter.toFahrenheit(weather.temp) else weather.temp
    val displayFeelsLike = if (isFahrenheit) UnitConverter.toFahrenheit(weather.feelsLike) else weather.feelsLike
    val displayMin = if (isFahrenheit) UnitConverter.toFahrenheit(weather.minTemp) else weather.minTemp
    val displayMax = if (isFahrenheit) UnitConverter.toFahrenheit(weather.maxTemp) else weather.maxTemp
    val unitSymbol = if (isFahrenheit) "°F" else "°C"

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCityClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = weather.city,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${displayTemp}$unitSymbol",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    imageVector = WeatherConditionHelper.getIcon(weather.conditionCode),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFB703)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(id = WeatherConditionHelper.getTitleRes(weather.conditionCode)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.feels_like, displayFeelsLike),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${displayMin}°/${displayMax}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Weather Hero Card Preview")
@Composable
fun WeatherHeroCardPreview() {
    MyFastTheme {
        WeatherHeroCard(
            weather = WeatherUI(
                city = "Krasnodar",
                temp = 24,
                feelsLike = 25,
                minTemp = 18,
                maxTemp = 28,
                conditionCode = 0
            ),
            onCityClick = {}
        )
    }
}
