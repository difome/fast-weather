package com.difome.fast

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.difome.fast.common.AppConstants
import com.difome.fast.data.local.SettingsPreferences
import com.difome.fast.ui.home.HomeScreen
import com.difome.fast.ui.settings.SettingsScreen
import com.difome.fast.ui.theme.MyFastTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsPrefs = remember { SettingsPreferences(applicationContext) }
            val themeMode by settingsPrefs.themeMode.collectAsState(initial = AppConstants.THEME_SYSTEM)
            val isDynamicColor by settingsPrefs.isDynamicColor.collectAsState(initial = true)
            val navController = rememberNavController()

            MyFastTheme(
                themeMode = themeMode,
                dynamicColor = isDynamicColor
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                name = "Fast",
                                onSettingsClick = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyFastTheme {
        HomeScreen("Android")
    }
}
