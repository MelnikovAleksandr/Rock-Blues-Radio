package ru.asmelnikov.rockbluesradio.presentation.utils

import android.content.res.Configuration
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isPortrait(): Boolean = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

@Composable
fun isLandscape(): Boolean = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

@Composable
fun Modifier.navigationBarsAndCutoutPaddingIfLandscape(): Modifier =
    if (isLandscape()) displayCutoutPadding().navigationBarsPadding() else this

@Composable
fun Modifier.navigationBarsPaddingIfLandscape(): Modifier =
    if (isLandscape()) navigationBarsPadding() else this
