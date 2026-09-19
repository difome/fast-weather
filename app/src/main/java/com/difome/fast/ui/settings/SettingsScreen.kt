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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
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
import com.difome.fast.common.AppConstants
import com.difome.fast.data.local.SettingsPreferences
import com.difome.fast.ui.theme.MyFastTheme
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsPrefs = remember { SettingsPreferences(context) }

    val isFahrenheit by settingsPrefs.isFahrenheit.collectAsState(initial = false)
    val windUnit by settingsPrefs.windUnit.collectAsState(initial = AppConstants.WIND_UNIT_MS)
    val pressureUnit by settingsPrefs.pressureUnit.collectAsState(initial = AppConstants.PRESSURE_UNIT_MBAR)
    val themeMode by settingsPrefs.themeMode.collectAsState(initial = AppConstants.THEME_SYSTEM)
    val isDynamicColor by settingsPrefs.isDynamicColor.collectAsState(initial = true)

    val currentAppLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: ""

    val appVersion = remember(context) {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: AppConstants.APP_VERSION
        } catch (e: Exception) {
            AppConstants.APP_VERSION
        }
    }

    SettingsScreenContent(
        isFahrenheit = isFahrenheit,
        windUnit = windUnit,
        pressureUnit = pressureUnit,
        themeMode = themeMode,
        isDynamicColor = isDynamicColor,
        currentAppLocale = currentAppLocale,
        appVersion = appVersion,
        onBackClick = { navController.popBackStack() },
        onToggleFahrenheit = {
            scope.launch { settingsPrefs.saveFahrenheit(!isFahrenheit) }
        },
        onToggleWindUnit = {
            scope.launch {
                val nextUnit = if (windUnit == AppConstants.WIND_UNIT_MS) AppConstants.WIND_UNIT_KMH else AppConstants.WIND_UNIT_MS
                settingsPrefs.saveWindUnit(nextUnit)
            }
        },
        onTogglePressureUnit = {
            scope.launch {
                val nextUnit = if (pressureUnit == AppConstants.PRESSURE_UNIT_MBAR) AppConstants.PRESSURE_UNIT_MMHG else AppConstants.PRESSURE_UNIT_MBAR
                settingsPrefs.savePressureUnit(nextUnit)
            }
        },
        onThemeModeSelected = { mode ->
            scope.launch { settingsPrefs.saveThemeMode(mode) }
        },
        onToggleDynamicColor = {
            scope.launch { settingsPrefs.saveDynamicColor(!isDynamicColor) }
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
    themeMode: String,
    isDynamicColor: Boolean,
    currentAppLocale: String,
    appVersion: String = AppConstants.APP_VERSION,
    onBackClick: () -> Unit,
    onToggleFahrenheit: () -> Unit,
    onToggleWindUnit: () -> Unit,
    onTogglePressureUnit: () -> Unit,
    onThemeModeSelected: (String) -> Unit,
    onToggleDynamicColor: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    var isLanguageDialogOpen by remember { mutableStateOf(false) }
    var isThemeDialogOpen by remember { mutableStateOf(false) }

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
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.temp_unit_title)) },
                    trailingContent = {
                        Text(
                            text = if (isFahrenheit) stringResource(id = R.string.unit_fahrenheit) else stringResource(id = R.string.unit_celsius),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.clickable { onToggleFahrenheit() }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.wind_unit_title)) },
                    trailingContent = {
                        Text(
                            text = if (windUnit == AppConstants.WIND_UNIT_KMH) stringResource(id = R.string.unit_kmh) else stringResource(id = R.string.unit_ms),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.clickable { onToggleWindUnit() }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.pressure_unit_title)) },
                    trailingContent = {
                        Text(
                            text = if (pressureUnit == AppConstants.PRESSURE_UNIT_MMHG) stringResource(id = R.string.unit_mmhg) else stringResource(id = R.string.unit_mbar),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.clickable { onTogglePressureUnit() }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.other_section_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                val currentLangDisplay = when (currentAppLocale) {
                    AppConstants.LANG_UK -> "Українська"
                    AppConstants.LANG_RU -> "Русский"
                    AppConstants.LANG_EN -> "English"
                    else -> stringResource(id = R.string.system_default)
                }

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.language_setting_title)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentLangDisplay,
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
                    },
                    modifier = Modifier.clickable { isLanguageDialogOpen = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                val currentThemeDisplay = when (themeMode) {
                    AppConstants.THEME_LIGHT -> stringResource(id = R.string.theme_light)
                    AppConstants.THEME_DARK -> stringResource(id = R.string.theme_dark)
                    AppConstants.THEME_AMOLED -> stringResource(id = R.string.theme_amoled)
                    else -> stringResource(id = R.string.theme_system)
                }

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.theme_setting_title)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentThemeDisplay,
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
                    },
                    modifier = Modifier.clickable { isThemeDialogOpen = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                ListItem(
                    headlineContent = { Text(stringResource(id = R.string.dynamic_color_title)) },
                    trailingContent = {
                        Switch(
                            checked = isDynamicColor,
                            onCheckedChange = { onToggleDynamicColor() }
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.app_version, appVersion),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
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

    if (isThemeDialogOpen) {
        ThemeSelectionDialog(
            currentThemeMode = themeMode,
            onThemeSelected = { mode ->
                onThemeModeSelected(mode)
                isThemeDialogOpen = false
            },
            onDismiss = { isThemeDialogOpen = false }
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLangTag: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val systemLang = Locale.getDefault().language
    val isSystemRussian = systemLang == AppConstants.LANG_RU

    val options = remember(isSystemRussian) {
        listOfNotNull(
            "" to null,
            AppConstants.LANG_UK to "Українська",
            if (isSystemRussian) AppConstants.LANG_RU to "Русский" else null,
            AppConstants.LANG_EN to "English"
        )
    }

    val systemDefaultText = stringResource(id = R.string.system_default)

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
                options.forEach { (tag, name) ->
                    val label = name ?: systemDefaultText
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

@Composable
fun ThemeSelectionDialog(
    currentThemeMode: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        AppConstants.THEME_SYSTEM to stringResource(id = R.string.theme_system),
        AppConstants.THEME_LIGHT to stringResource(id = R.string.theme_light),
        AppConstants.THEME_DARK to stringResource(id = R.string.theme_dark),
        AppConstants.THEME_AMOLED to stringResource(id = R.string.theme_amoled)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.theme_setting_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { (mode, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(mode) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (currentThemeMode == mode),
                            onClick = { onThemeSelected(mode) }
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

@Preview(showBackground = true, name = "Settings Screen Preview", heightDp = 700)
@Composable
fun SettingsScreenPreview() {
    MyFastTheme {
        SettingsScreenContent(
            isFahrenheit = false,
            windUnit = AppConstants.WIND_UNIT_MS,
            pressureUnit = AppConstants.PRESSURE_UNIT_MBAR,
            themeMode = AppConstants.THEME_SYSTEM,
            isDynamicColor = true,
            currentAppLocale = AppConstants.LANG_UK,
            appVersion = "1.0.0",
            onBackClick = {},
            onToggleFahrenheit = {},
            onToggleWindUnit = {},
            onTogglePressureUnit = {},
            onThemeModeSelected = {},
            onToggleDynamicColor = {},
            onLanguageSelected = {}
        )
    }
}
