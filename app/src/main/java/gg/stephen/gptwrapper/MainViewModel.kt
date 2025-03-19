package gg.stephen.gptwrapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.ai.openai.models.ChatCompletionsOptions
import com.azure.ai.openai.models.ChatRequestMessage
import com.azure.ai.openai.models.ChatRequestUserMessage
import com.azure.ai.openai.models.ChatRequestAssistantMessage
import com.azure.core.credential.AzureKeyCredential
import gg.stephen.gptwrapper.chat.ChatItem
import gg.stephen.gptwrapper.chat.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _conversationHistory: MutableStateFlow<List<ChatItem>> = MutableStateFlow(emptyList())
    val conversationHistory: StateFlow<List<ChatItem>> = _conversationHistory.asStateFlow()

    private val _selectedModel: MutableStateFlow<String> = MutableStateFlow("gpt-4o-mini")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val openAIClient = OpenAIClientBuilder()
        .endpoint(BuildConfig.AZURE_OPENAI_ENDPOINT)
        .credential(AzureKeyCredential(BuildConfig.AZURE_OPENAI_KEY))
        .buildClient()

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        _conversationHistory.value = emptyList()
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

                // Convert conversation history to Azure OpenAI format
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
}