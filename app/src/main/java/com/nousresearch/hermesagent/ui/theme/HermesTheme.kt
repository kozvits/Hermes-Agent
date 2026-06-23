package com.nousresearch.hermesagent.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Brand Colors ──
val HermesPrimary = Color(0xFF6C63FF)
val HermesPrimaryDark = Color(0xFF5A52D5)
val HermesSecondary = Color(0xFF03DAC6)
val HermesTertiary = Color(0xFFE8A0FF)
val HermesBackground = Color(0xFFF8F9FE)
val HermesSurface = Color(0xFFFFFFFF)
val HermesError = Color(0xFFCF6679)
val HermesOnPrimary = Color(0xFFFFFFFF)

// Dark mode
val HermesDarkBackground = Color(0xFF0D0D1A)
val HermesDarkSurface = Color(0xFF1A1A2E)
val HermesDarkSurfaceVariant = Color(0xFF252540)
val HermesDarkOnBackground = Color(0xFFE8E8F0)

// ── Color Schemes ──
private val LightColorScheme = lightColorScheme(
    primary = HermesPrimary,
    onPrimary = HermesOnPrimary,
    primaryContainer = Color(0xFFE8E0FF),
    onPrimaryContainer = Color(0xFF2D1B69),
    secondary = HermesSecondary,
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFFA7F0EC),
    onSecondaryContainer = Color(0xFF00201E),
    tertiary = HermesTertiary,
    onTertiary = Color(0xFF3B104E),
    tertiaryContainer = Color(0xFFFAD8FF),
    onTertiaryContainer = Color(0xFF240936),
    background = HermesBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = HermesSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = HermesError,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFD0BCFF),
    scrim = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB0A8FF),
    onPrimary = Color(0xFF2D1B69),
    primaryContainer = Color(0xFF4B3D9E),
    onPrimaryContainer = Color(0xFFE8E0FF),
    secondary = Color(0xFF69F0E4),
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF00504B),
    onSecondaryContainer = Color(0xFFA7F0EC),
    tertiary = Color(0xFFE4B0FF),
    onTertiary = Color(0xFF3B104E),
    tertiaryContainer = Color(0xFF5C2570),
    onTertiaryContainer = Color(0xFFFAD8FF),
    background = HermesDarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = HermesDarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = HermesDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF313033),
    inversePrimary = Color(0xFF6C63FF),
    scrim = Color.Black,
)

// ── Status Colors ──
object HermesStatusColors {
    val connected = Color(0xFF4CAF50)
    val disconnected = Color(0xFF9E9E9E)
    val error = Color(0xFFEF5350)
    val streaming = Color(0xFF42A5F5)
    val thinking = Color(0xFFFF9800)
    val user = Color(0xFF6C63FF)
    val assistant = Color(0xFF1A1A2E)
}

// ── Typography ──

val HermesTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ── Shapes ──

val HermesShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// ── Dimensions ──
object Dimens {
    val padding_xs = 4.dp
    val padding_sm = 8.dp
    val padding_md = 12.dp
    val padding_lg = 16.dp
    val padding_xl = 24.dp
    val padding_xxl = 32.dp

    val avatar_size = 36.dp
    val icon_size = 24.dp
    val icon_size_sm = 20.dp
    val fab_size = 56.dp
    val min_touch = 48.dp

    val card_elevation = 2.dp
    val dialog_elevation = 8.dp
    val drawer_width = 300.dp
    val max_chat_width = 720.dp
    val message_border_radius = 16.dp
    val input_field_height = 56.dp
}

// ── Theme ──
@Composable
fun HermesAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HermesTypography,
        shapes = HermesShapes,
        content = content,
    )
}
