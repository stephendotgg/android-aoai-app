package gg.stephen.gptwrapper.ui.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.stephen.gptwrapper.MainViewModel
import gg.stephen.gptwrapper.R

@Composable
fun ChatBottomBar(mainViewModel: MainViewModel) {
    var prompt by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A2829).copy(alpha = 0.8f),
                        Color(0xFF2B292A).copy(alpha = 0.65f)
                    ),
                    startY = 0f,
                    endY = 500f
                ),
                shape = RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .drawWithCache {
                onDrawBehind {
                    val cornerRadius = 30.dp.toPx()
                    val borderWidth = 1.dp.toPx()

                    val path = Path().apply {
                        moveTo(cornerRadius, 0f)
                        lineTo(size.width - cornerRadius, 0f)
                        arcTo(
                            rect = Rect(
                                left = size.width - cornerRadius * 2,
                                top = 0f,
                                right = size.width,
                                bottom = cornerRadius * 2
                            ),
                            startAngleDegrees = 270f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                        moveTo(0f, cornerRadius)
                        arcTo(
                            rect = Rect(
                                left = 0f,
                                top = 0f,
                                right = cornerRadius * 2,
                                bottom = cornerRadius * 2
                            ),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color.Gray,
                        style = Stroke(width = borderWidth)
                    )
                }
            }
            .padding(16.dp, 14.dp, 16.dp, 35.dp)
    ) {
        // Text input field
        BasicTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 10.dp, 4.dp, 5.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (prompt.isNotBlank()) {
                        mainViewModel.sendPrompt(prompt)
                        prompt = ""
                    }
                }
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (prompt.isEmpty()) {
                        Text(
                            text = "Type a message...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        // Button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                ActionButton(
                    icon = R.drawable.plus_solid,
                    contentDescription = "Add",
                    onClick = { }
                )

                ActionButton(
                    icon = R.drawable.magnifying_glass_solid,
                    contentDescription = "Search",
                    text = "Search",
                    onClick = { }
                )

                ActionButton(
                    icon = R.drawable.lightbulb_regular,
                    contentDescription = "Reason",
                    text = "Reason",
                    onClick = { }
                )
            }

            // Send button
            SendButton(
                onClick = {
                    if (prompt.isNotBlank()) {
                        mainViewModel.sendPrompt(prompt)
                        prompt = ""
                    }
                }
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: Int,
    contentDescription: String,
    text: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = if (text != null) RoundedCornerShape(18.dp) else CircleShape
            )
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.height(36.dp),
            contentPadding = if (text != null) PaddingValues(horizontal = 12.dp, vertical = 0.dp) else PaddingValues(0.dp),
            shape = if (text != null) RoundedCornerShape(18.dp) else CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Gray
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            if (text != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = contentDescription,
                        modifier = Modifier.size(14.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    )
                    Text(
                        text = text,
                        color = Color.White
                    )
                }
            } else {
                Image(
                    painter = painterResource(icon),
                    contentDescription = contentDescription,
                    modifier = Modifier.size(17.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
            }
        }
    }
}

@Composable
private fun SendButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = CircleShape
            )
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(36.dp),
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.paper_plane),
                contentDescription = "Send",
                modifier = Modifier.size(18.dp)
            )
        }
    }
} 