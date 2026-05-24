package ru.asmelnikov.rockbluesradio.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember

@Composable
fun AppUtils(
    appDimens: Dimens,
    content: @Composable () -> Unit
) {

    val dimens = remember {
        appDimens
    }

    CompositionLocalProvider(LocalAppDimens provides dimens) {
        content()
    }

}

val LocalAppDimens = compositionLocalOf {
    CompactDimens
}

val dimens
    @Composable
    get() = LocalAppDimens.current