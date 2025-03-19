package gg.stephen.gptwrapper.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun ChatBackgroundGradient() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(500.dp)
        ) {
            val boxSize = remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        boxSize.value = coordinates.size.toSize()
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFFFF1493).copy(alpha = 0.7f),
                                0.3f to Color(0xFFFF1493).copy(alpha = 0.5f),
                                0.5f to Color(0xFFFF1493).copy(alpha = 0.3f),
                                0.7f to Color(0xFFFF1493).copy(alpha = 0.15f),
                                0.85f to Color(0xFFFF1493).copy(alpha = 0.05f),
                                1.0f to Color(0x00000000)
                            ),
                            center = androidx.compose.ui.geometry.Offset(
                                x = boxSize.value.width / 2f,
                                y = boxSize.value.height
                            ),
                            radius = 1200f
                        )
                    )
            )
        }
    }
} 