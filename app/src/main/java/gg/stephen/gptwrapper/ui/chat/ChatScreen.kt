package gg.stephen.gptwrapper.ui.chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gg.stephen.gptwrapper.MainViewModel
import gg.stephen.gptwrapper.ui.chat.components.ChatBackgroundGradient
import gg.stephen.gptwrapper.ui.chat.components.ChatBottomBar
import gg.stephen.gptwrapper.ui.chat.components.ChatContent
import gg.stephen.gptwrapper.ui.chat.components.ChatHistorySidebar
import gg.stephen.gptwrapper.ui.chat.components.ChatTopBar

@Composable
fun ChatScreen(mainViewModel: MainViewModel) {
    val conversationHistory by mainViewModel.conversationHistory.collectAsState()
    val chatHistories by mainViewModel.chatHistories.collectAsState()
    val isSidebarOpen by mainViewModel.isSidebarOpen.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background gradient - only show when conversation is empty
        if (conversationHistory.isEmpty()) {
            ChatBackgroundGradient()
        }

        // Main content
        Column(modifier = Modifier.fillMaxSize()) {
            ChatTopBar(mainViewModel)
            Box(modifier = Modifier.weight(1f)) {
                ChatContent(mainViewModel)
            }
            ChatBottomBar(mainViewModel)
        }

        // Sidebar
        val sidebarAlpha by animateFloatAsState(
            targetValue = if (isSidebarOpen) 1f else 0f,
            label = "sidebar_alpha"
        )

        if (isSidebarOpen) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .alpha(sidebarAlpha),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                ChatHistorySidebar(
                    chatHistories = chatHistories,
                    onClose = { mainViewModel.toggleSidebar() },
                    onDelete = { mainViewModel.deleteChatHistory(it) },
                    onChatSelected = { mainViewModel.loadChatHistory(it) }
                )
            }
        }
    }
} 