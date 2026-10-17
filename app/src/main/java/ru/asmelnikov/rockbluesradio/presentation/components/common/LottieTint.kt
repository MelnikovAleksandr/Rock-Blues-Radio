package ru.asmelnikov.rockbluesradio.presentation.components.common

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun rememberLottieTint(color: Color): LottieDynamicProperties = rememberLottieDynamicProperties(
    rememberLottieDynamicProperty(
        property = LottieProperty.COLOR_FILTER,
        value = PorterDuffColorFilter(color.toArgb(), PorterDuff.Mode.SRC_ATOP),
        keyPath = arrayOf("**"),
    ),
)
