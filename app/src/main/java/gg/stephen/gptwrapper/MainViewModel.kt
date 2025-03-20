package gg.stephen.gptwrapper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.ChatCompletionsOptions
import com.azure.ai.openai.models.ChatRequestMessage
import com.azure.ai.openai.models.ChatRequestUserMessage
import com.azure.ai.openai.models.ChatRequestAssistantMessage
import com.azure.core.credential.AzureKeyCredential
import gg.stephen.gptwrapper.chat.ChatItem
import gg.stephen.gptwrapper.chat.User
import gg.stephen.gptwrapper.data.AppDatabase
import gg.stephen.gptwrapper.data.ChatHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val chatHistoryDao = database.chatHistoryDao()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _conversationHistory: MutableStateFlow<List<ChatItem>> = MutableStateFlow(emptyList())
    val conversationHistory: StateFlow<List<ChatItem>> = _conversationHistory.asStateFlow()

    private val _selectedModel: MutableStateFlow<String> = MutableStateFlow("gpt-4o-mini")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _chatHistories: MutableStateFlow<List<ChatHistory>> = MutableStateFlow(emptyList())
    val chatHistories: StateFlow<List<ChatHistory>> = _chatHistories.asStateFlow()

    private val _isSidebarOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSidebarOpen: StateFlow<Boolean> = _isSidebarOpen.asStateFlow()

    private val openAIClient = OpenAIClientBuilder()
        .endpoint(BuildConfig.AZURE_OPENAI_ENDPOINT)
        .credential(AzureKeyCredential(BuildConfig.AZURE_OPENAI_KEY))
        .buildClient()

    init {
        viewModelScope.launch {
            chatHistoryDao.getAllChatHistories().collect { histories ->
                _chatHistories.value = histories
            }
        }
    }

    fun toggleSidebar() {
        _isSidebarOpen.value = !_isSidebarOpen.value
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        _conversationHistory.value = emptyList()
    }

    fun loadChatHistory(chatHistory: ChatHistory) {
        _conversationHistory.value = chatHistory.conversation
        _selectedModel.value = chatHistory.model
        _isSidebarOpen.value = false
    }

    fun sendPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val currentHistory = _conversationHistory.value.toMutableList()
        currentHistory.add(ChatItem(User.USER, prompt))
        _conversationHistory.value = currentHistory
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompts = ArrayList<ChatRequestMessage>()

                for (chatItem in currentHistory) {
                    when (chatItem.user) {
                        User.USER -> prompts.add(ChatRequestUserMessage(chatItem.message))
                        User.ASSISTANT -> prompts.add(ChatRequestAssistantMessage(chatItem.message))
                        User.SYSTEM -> {}
                    }
                }

                val options = ChatCompletionsOptions(prompts)
                    .setMaxTokens(800)
                    .setTemperature(0.7)
                    .setTopP(0.95)

                val chatCompletions = openAIClient.getChatCompletions(_selectedModel.value, options)

                if (chatCompletions != null && !chatCompletions.getChoices().isEmpty()) {
                    val outputContent = chatCompletions.getChoices().get(0).getMessage().getContent()
                    if (outputContent != null) {
                        val updatedHistory = currentHistory.toMutableList()
                        updatedHistory.add(ChatItem(User.ASSISTANT, outputContent))
                        _conversationHistory.value = updatedHistory
                        _uiState.value = UiState.Success(outputContent)

                        // Save chat history
                        if (currentHistory.size == 1) { // Only save new conversations
                            val title = prompt.take(50) + if (prompt.length > 50) "..." else ""
                            val chatHistory = ChatHistory(
                                title = title,
                                lastMessage = outputContent,
                                timestamp = Date(),
                                model = _selectedModel.value,
                                conversation = updatedHistory
                            )
                            chatHistoryDao.insertChatHistory(chatHistory)
                        } else {
                            // Update existing chat history
                            val existingHistory = _chatHistories.value.find { it.conversation == currentHistory }
                            if (existingHistory != null) {
                                val updatedChatHistory = existingHistory.copy(
                                    lastMessage = outputContent,
                                    timestamp = Date(),
                                    conversation = updatedHistory
                                )
                                chatHistoryDao.updateChatHistory(updatedChatHistory)
                            }
                        }
                    } else {
                        _uiState.value = UiState.Error("Empty response received")
                    }
                } else {
                    _uiState.value = UiState.Error("No response generated")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    fun clearConversation() {
        _conversationHistory.value = emptyList()
        _uiState.value = UiState.Initial
    }

    fun deleteChatHistory(chatHistory: ChatHistory) {
        viewModelScope.launch {
            chatHistoryDao.deleteChatHistory(chatHistory)
        }
    }
}