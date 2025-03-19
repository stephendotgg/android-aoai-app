package gg.stephen.gptwrapper.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gg.stephen.gptwrapper.MainViewModel
import gg.stephen.gptwrapper.UiState
import gg.stephen.gptwrapper.chat.User

@Composable
fun ChatContent(mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()
    val conversationHistory by mainViewModel.conversationHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (uiState is UiState.Loading && conversationHistory.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Welcome message if conversation history is empty
                if (conversationHistory.isEmpty()) {
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
                    conversationHistory.forEach { chatItem ->
                        ChatBubble(
                            message = chatItem.message,
                            isUserMessage = chatItem.user == User.USER
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Loading indicator for new responses
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

                Spacer(modifier = Modifier.height(16.dp))
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
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.08f)
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