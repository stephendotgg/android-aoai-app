package gg.stephen.gptwrapper

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import gg.stephen.gptwrapper.chat.User

@Composable
fun BakingScreen(
    bakingViewModel: BakingViewModel = viewModel()
) {
    val selectedImage = remember { mutableIntStateOf(0) }
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val uiState by bakingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var prompt by rememberSaveable { mutableStateOf("") }

    // This Box contains everything - including the gradient that will be positioned at the very bottom
    Box(modifier = Modifier.fillMaxSize()) {
        // First, place the gradient at the bottom of the screen
        // The gradient will appear below everything else
        if (bakingViewModel.conversationHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                // Use onGloballyPositioned to get the container size
                val density = LocalDensity.current
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
                                // Place center at the bottom middle of the box
                                center = androidx.compose.ui.geometry.Offset(
                                    x = boxSize.value.width / 2f,
                                    y = boxSize.value.height
                                ),
                                radius = 1200f  // Increased from 1000f to make the gradient even larger
                            )
                        )
                )
            }
        }

        // Then place the main content column above the gradient
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar remains the same
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 33.dp, 16.dp, 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Your existing top bar code...
                Image(
                    painter = painterResource(R.drawable.bars_solid),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )

                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf("Gemini 2.0 Flash") }
                val options = listOf("Gemini 2.0 Flash", "GPT-4o", "Claude 3.7 Sonnet")

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF2C282B),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color = Color(0xFF1FFE8),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { expanded = true }
                        .padding(15.dp, 0.dp)
                ) {
                    Row(
                        modifier = Modifier.height(36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = selectedOption,
                            color = Color(0xFFFFB1E3),
                        )
                        Image(
                            painter = painterResource(R.drawable.chevron_down_solid),
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(12.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFFFB1E3))
                        )
                    }

                    // Dropdown menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Image(
                    painter = painterResource(R.drawable.avatar),
                    contentDescription = "Dropdown",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
            }

            // Content area - updated to show conversation history
            // Content area - updated to show conversation history or welcome message
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (uiState is UiState.Loading && bakingViewModel.conversationHistory.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        // Show welcome message if conversation history is empty
                        if (bakingViewModel.conversationHistory.isEmpty()) {
                            Text(
                                text = "Good evening, Stephen",
                                color = Color.White,
                                fontSize = 40.sp,
                                lineHeight = 50.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
                            )
                        } else {
                            // Show conversation history
                            bakingViewModel.conversationHistory.forEach { chatItem ->
                                ChatBubble(
                                    message = chatItem.message,
                                    isUserMessage = chatItem.user == User.USER
                                )

                                // Add spacing between messages
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Show loading indicator at the end if we're loading a new response
                        if (uiState is UiState.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    color = Color(0xFFFFB1E3),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        // Error handling
                        if (uiState is UiState.Error) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color(0xFF7A3C5A).copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Error: ${(uiState as UiState.Error).errorMessage}",
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Spacer at the bottom to ensure content is scrollable above the input field
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Bottom bar with translucent background - remains mostly the same
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
                                bakingViewModel.sendPrompt(prompt)
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

                // Small spacing between rows
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(vertical = 8.dp))

                // Button row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        // Plus button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = CircleShape
                                )
                                .align(Alignment.CenterVertically)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Gray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.plus_solid),
                                    contentDescription = "Add",
                                    modifier = Modifier.size(17.dp),
                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                                )
                            }
                        }

                        // Search button
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .align(Alignment.CenterVertically)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Gray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.magnifying_glass_solid),
                                        contentDescription = "Search",
                                        modifier = Modifier.size(14.dp),
                                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                                    )

                                    Text(
                                        text = "Search",
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Reason button
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .align(Alignment.CenterVertically)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Gray
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.lightbulb_regular),
                                        contentDescription = "Reason",
                                        modifier = Modifier.size(14.dp),
                                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                                    )

                                    Text(
                                        text = "Reason",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Send button
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
                            .align(Alignment.CenterVertically)
                    ) {
                        Button(
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    bakingViewModel.sendPrompt(prompt)
                                    prompt = ""
                                }
                            },
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
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String,
    isUserMessage: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (isUserMessage) {
            // User message - Right-aligned bubble with 75% max width and translucent gradient
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFF2C282B),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .widthIn(max = (LocalConfiguration.current.screenWidthDp * 0.75).dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),  // More opaque at top
                                Color.White.copy(alpha = 0.08f)    // More transparent at bottom
                            )
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        } else {
            // LLM message - Full width white text without bubble
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun BakingScreenPreview() {
    BakingScreen()
}