package ru.asmelnikov.rockbluesradio.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalAppDimens = compositionLocalOf { CompactDimens }

val dimens: Dimens
    @Composable
    get() = LocalAppDimens.current

@Composable
fun ProvideDimens(
    dimens: Dimens,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppDimens provides dimens, content = content)
}
