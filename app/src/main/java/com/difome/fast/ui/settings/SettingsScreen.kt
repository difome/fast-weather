package com.difome.fast.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.difome.fast.R
import com.difome.fast.data.local.SettingsPreferences
import com.difome.fast.ui.theme.MyFastTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsPrefs = remember { SettingsPreferences(context) }

    val isFahrenheit by settingsPrefs.isFahrenheit.collectAsState(initial = false)
    val windUnit by settingsPrefs.windUnit.collectAsState(initial = "ms")
    val pressureUnit by settingsPrefs.pressureUnit.collectAsState(initial = "mbar")

    val currentAppLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: ""

    SettingsScreenContent(
        isFahrenheit = isFahrenheit,
        windUnit = windUnit,
        pressureUnit = pressureUnit,
        currentAppLocale = currentAppLocale,
        onBackClick = { navController.popBackStack() },
        onToggleFahrenheit = {
            scope.launch { settingsPrefs.saveFahrenheit(!isFahrenheit) }
        },
        onToggleWindUnit = {
            scope.launch {
                val nextUnit = if (windUnit == "ms") "kmh" else "ms"
                settingsPrefs.saveWindUnit(nextUnit)
            }
        },
        onTogglePressureUnit = {
            scope.launch {
                val nextUnit = if (pressureUnit == "mbar") "mmhg" else "mbar"
                settingsPrefs.savePressureUnit(nextUnit)
            }
        },
        onLanguageSelected = { langTag ->
            setAppLanguage(langTag)
        }
    )
}

@Composable
fun SettingsScreenContent(
    isFahrenheit: Boolean,
    windUnit: String,
    pressureUnit: String,
    currentAppLocale: String,
    onBackClick: () -> Unit,
    onToggleFahrenheit: () -> Unit,
    onToggleWindUnit: () -> Unit,
    onTogglePressureUnit: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var isLanguageDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.units_section_title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingClickableRow(
            title = stringResource(id = R.string.temp_unit_title),
            valueText = if (isFahrenheit) stringResource(id = R.string.unit_fahrenheit) else stringResource(id = R.string.unit_celsius),
            onClick = onToggleFahrenheit
        )

        SettingClickableRow(
            title = stringResource(id = R.string.wind_unit_title),
            valueText = if (windUnit == "kmh") stringResource(id = R.string.unit_kmh) else stringResource(id = R.string.unit_ms),
            onClick = onToggleWindUnit
        )

        SettingClickableRow(
            title = stringResource(id = R.string.pressure_unit_title),
            valueText = if (pressureUnit == "mmhg") stringResource(id = R.string.unit_mmhg) else stringResource(id = R.string.unit_mbar),
            onClick = onTogglePressureUnit
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.other_section_title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val currentLangDisplay = when (currentAppLocale) {
            "uk" -> "Українська 🇺🇦"
            "ru" -> "Русский 🇷🇺"
            "en" -> "English 🇬🇧"
            else -> stringResource(id = R.string.system_default)
        }

        SettingClickableRow(
            title = stringResource(id = R.string.language_setting_title),
            valueText = currentLangDisplay,
            onClick = { isLanguageDialogOpen = true }
        )
    }

    if (isLanguageDialogOpen) {
        LanguageSelectionDialog(
            currentLangTag = currentAppLocale,
            onLanguageSelected = { langTag ->
                onLanguageSelected(langTag)
                isLanguageDialogOpen = false
            },
            onDismiss = { isLanguageDialogOpen = false }
        )
    }
}

@Composable
fun SettingClickableRow(
    title: String,
    valueText: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLangTag: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "" to stringResource(id = R.string.system_default),
        "uk" to "Українська 🇺🇦",
        "ru" to "Русский 🇷🇺",
        "en" to "English 🇬🇧"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.language_setting_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { (tag, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(tag) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (currentLangTag == tag),
                            onClick = { onLanguageSelected(tag) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.back_button))
            }
        }
    )
}

private fun setAppLanguage(languageTag: String) {
    val appLocales = if (languageTag.isEmpty()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(languageTag)
    }
    AppCompatDelegate.setApplicationLocales(appLocales)
}

@Preview(showBackground = true, name = "Settings Screen Preview")
@Composable
fun SettingsScreenPreview() {
    MyFastTheme {
        SettingsScreenContent(
            isFahrenheit = false,
            windUnit = "ms",
            pressureUnit = "mbar",
            currentAppLocale = "uk",
            onBackClick = {},
            onToggleFahrenheit = {},
            onToggleWindUnit = {},
            onTogglePressureUnit = {},
            onLanguageSelected = {}
        )
    }
}
