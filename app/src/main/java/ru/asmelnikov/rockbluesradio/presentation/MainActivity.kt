package ru.asmelnikov.rockbluesradio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.asmelnikov.rockbluesradio.presentation.app.MainViewModel
import ru.asmelnikov.rockbluesradio.presentation.app.RadioApp
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            RockBluesRadioTheme {
                RadioApp(viewModel = mainViewModel)
            }
        }
    }
}
