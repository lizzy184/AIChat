
package com.example.aichatapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * 深色主题
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),

    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),

    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99)
)

/**
 * 浅色主题
 * 使用暖灰白背景，降低纯白页面带来的刺眼感
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),

    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),

    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),

    background = Color(0xFFF5F3EF),
    onBackground = Color(0xFF292824),

    surface = Color(0xFFFAF9F6),
    onSurface = Color(0xFF292824),

    surfaceVariant = Color(0xFFEAE6DF),
    onSurfaceVariant = Color(0xFF514D46),

    outline = Color(0xFF817B72)
)

/**
 * AI 聊天应用主题
 *
 * darkTheme:
 * true  -> 深色主题
 * false -> 浅色主题
 *
 * dynamicColor:
 * false -> 使用应用自定义配色
 * true  -> Android 12 及以上版本可以使用系统动态配色
 */
@Composable
fun AIchatAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
