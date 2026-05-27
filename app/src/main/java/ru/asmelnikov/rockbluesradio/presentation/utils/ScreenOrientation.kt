package ru.asmelnikov.rockbluesradio.presentation.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isPortrait(): Boolean {
    val configuration by rememberUpdatedState(LocalConfiguration.current)
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

@Composable
fun isLandScape(): Boolean {
    val configuration by rememberUpdatedState(LocalConfiguration.current)
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun Modifier.navigationBarsAndCutoutPaddingIfLandscape(): Modifier =
    if (isLandScape()) {
        this
            .displayCutoutPadding()
            .navigationBarsPadding()
    } else {
        this
    }

@Composable
fun Modifier.navigationBarsPaddingIfLandscape(): Modifier =
    if (isLandScape()) {
        this
            .navigationBarsPadding()
    } else {
        this
    }

@Composable
fun Modifier.navigationBarsPaddingIfPortrait(): Modifier =
    if (isPortrait()) {
        this
            .navigationBarsPadding()
    } else {
        this
    }

