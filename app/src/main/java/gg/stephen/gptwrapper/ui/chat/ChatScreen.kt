package gg.stephen.gptwrapper.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import gg.stephen.gptwrapper.MainViewModel
import gg.stephen.gptwrapper.ui.chat.components.ChatBackgroundGradient
import gg.stephen.gptwrapper.ui.chat.components.ChatBottomBar
import gg.stephen.gptwrapper.ui.chat.components.ChatContent
import gg.stephen.gptwrapper.ui.chat.components.ChatTopBar

@Composable
fun ChatScreen(mainViewModel: MainViewModel) {
    val conversationHistory by mainViewModel.conversationHistory.collectAsState()

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
    }
} 