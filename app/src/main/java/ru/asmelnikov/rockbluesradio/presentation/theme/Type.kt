package ru.asmelnikov.rockbluesradio.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import ru.asmelnikov.rockbluesradio.R
import androidx.compose.ui.unit.sp

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Yanone Kaffeesatz"),
        fontProvider = provider,
    )
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Anonymous Pro"),
        fontProvider = provider,
    )
)

val CompactTypography = Typography(
    displayLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 26.sp),
    displayMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 24.sp),
    displaySmall = TextStyle(fontFamily = displayFontFamily, fontSize = 22.sp),
    headlineLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 20.sp),
    headlineMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 18.sp),
    headlineSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 16.sp),
    titleLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 15.sp),
    titleMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 14.sp),
    titleSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 13.sp),
    bodyLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 13.sp),
    labelSmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 12.sp)
)

val MediumTypography = Typography(
    displayLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 30.sp),
    displayMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 28.sp),
    displaySmall = TextStyle(fontFamily = displayFontFamily, fontSize = 26.sp),
    headlineLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 22.sp),
    headlineSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 18.sp),
    bodyMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    bodySmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    labelMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp),
    labelSmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 13.sp)
)

val ExpandedTypography = Typography(
    displayLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 34.sp),
    displayMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 32.sp),
    displaySmall = TextStyle(fontFamily = displayFontFamily, fontSize = 30.sp),
    headlineLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 26.sp),
    headlineSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 20.sp),
    titleSmall = TextStyle(fontFamily = displayFontFamily, fontSize = 18.sp),
    bodyLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 20.sp),
    bodyMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 18.sp),
    bodySmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    labelLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 18.sp),
    labelMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    labelSmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp)
)

