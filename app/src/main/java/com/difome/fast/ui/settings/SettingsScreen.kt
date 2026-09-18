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
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.difome.fast.R

@Composable
fun SettingsScreen(navController: NavController) {
    var selectedLang by rememberSaveable { mutableStateOf("en") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clickable { selectedLang = "ru" }
                .padding(8.dp)
        ) {
            RadioButton(
                selected = (selectedLang == "ru"),
                onClick = { selectedLang = "ru" }
            )
            Text(text = "Русский 🇷🇺")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clickable { selectedLang = "en" }
                .padding(8.dp)
        ) {
            RadioButton(
                selected = (selectedLang == "en"),
                onClick = { selectedLang = "en" }
            )
            Text(text = "English 🇬🇧")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clickable { selectedLang = "uk" }
                .padding(8.dp)
        ) {
            RadioButton(
                selected = (selectedLang == "uk"),
                onClick = { selectedLang = "uk" }
            )
            Text(text = "Українська 🇺🇦")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                setAppLanguage(selectedLang)
            }
        ) {
            Text(text = "Сохранить")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка Назад
        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(text = stringResource(id = R.string.back_button))
        }
    }
}

private fun setAppLanguage(languageTag: String) {
    val appLocales = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(appLocales)
}