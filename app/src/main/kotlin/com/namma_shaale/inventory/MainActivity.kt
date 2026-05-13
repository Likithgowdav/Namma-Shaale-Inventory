package com.namma_shaale.inventory

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma_shaale.inventory.presentation.navigation.NavGraph
import com.namma_shaale.inventory.presentation.settings.SettingsViewModel
import com.namma_shaale.inventory.presentation.theme.NammaShaaleTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.Scaffold
import com.namma_shaale.inventory.presentation.navigation.BottomNavigationBar
import com.namma_shaale.inventory.data.repository.AuthRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val languageCode by settingsViewModel.languageCode.collectAsState()
            
            // Apply language change
            LaunchedEffect(languageCode) {
                val appLocales: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
                AppCompatDelegate.setApplicationLocales(appLocales)
            }
            
            val startDestination = if (authRepository.isUserLoggedIn()) "dashboard" else "login"
            
            NammaShaaleTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    
                    val showBottomBar = currentRoute !in listOf("login", "signup")

                    Scaffold(
                        bottomBar = { 
                            if (showBottomBar) {
                                BottomNavigationBar(navController = navController) 
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
