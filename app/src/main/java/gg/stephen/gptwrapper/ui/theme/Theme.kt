package gg.stephen.gptwrapper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF0A0A0A),
    onPrimary = Color.Green,
    onSecondary = Color.Green,
    onTertiary = Color.Green,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun GPTWrapperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}