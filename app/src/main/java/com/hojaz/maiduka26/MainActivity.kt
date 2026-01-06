package com.hojaz.maiduka26

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.presentantion.navigation.NavGraph
import com.hojaz.maiduka26.presentantion.navigation.Screen
import com.hojaz.maiduka26.presentantion.theme.MaiDuka26Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaiDuka26Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by preferencesManager.isLoggedIn.collectAsState(initial = false)
                    val navController = rememberNavController()

                    NavGraph(
                        navController = navController,
                        startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route
                    )
                }
            }
        }
    }
}