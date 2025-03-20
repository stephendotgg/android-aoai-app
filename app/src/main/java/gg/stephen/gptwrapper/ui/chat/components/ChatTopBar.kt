package gg.stephen.gptwrapper.ui.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import gg.stephen.gptwrapper.MainViewModel
import gg.stephen.gptwrapper.R

@Composable
fun ChatTopBar(mainViewModel: MainViewModel) {
    val selectedModel by mainViewModel.selectedModel.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val models = listOf(
        "gpt-4" to "GPT-4",
        "gpt-4o-mini" to "GPT-4o Mini",
        "gpt-35-turbo" to "GPT-3.5 Turbo"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 33.dp, 16.dp, 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Menu button
        Image(
            painter = painterResource(R.drawable.bars_solid),
            contentDescription = "Menu",
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.CenterVertically)
                .clickable { mainViewModel.toggleSidebar() },
            colorFilter = ColorFilter.tint(Color.White)
        )

        // Model selector dropdown
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
                    text = models.find { it.first == selectedModel }?.second ?: "GPT-4",
                    color = Color(0xFFFFB1E3),
                )
                Image(
                    painter = painterResource(R.drawable.chevron_down_solid),
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(12.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFFFB1E3))
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                models.forEach { (id, displayName) ->
                    DropdownMenuItem(
                        text = { Text(text = displayName) },
                        onClick = {
                            mainViewModel.setSelectedModel(id)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Avatar
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        )
    }
} 