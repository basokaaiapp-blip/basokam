package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.model.AICapability
import com.example.ui.components.KurdishRtlProvider
import com.example.ui.screens.AiComparisonScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImageStudioScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ModelManagerScreen
import com.example.ui.screens.ProviderManagerScreen
import com.example.ui.screens.RouterPlaygroundScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.UsageAnalyticsScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted

sealed class NavDestination(val route: String, val titleKu: String, val icon: ImageVector) {
    object Home : NavDestination("home", "سەرەکی", Icons.Default.Home)
    object Chat : NavDestination("chat", "گفتوگۆ", Icons.Default.Chat)
    object Tools : NavDestination("tools", "ئامرازەکان", Icons.Default.Build)
    object History : NavDestination("history", "مێژوو", Icons.Default.History)
    object Settings : NavDestination("settings", "ڕێکخستن", Icons.Default.Settings)
}

@Composable
fun MainAppScaffold(
    repository: BasokaRepository
) {
    var currentRoute by remember { mutableStateOf("home") }
    var chatInitialPrompt by remember { mutableStateOf("") }
    var chatExplicitCapability by remember { mutableStateOf<AICapability?>(null) }

    val destinations = listOf(
        NavDestination.Home,
        NavDestination.Chat,
        NavDestination.Tools,
        NavDestination.History,
        NavDestination.Settings
    )

    KurdishRtlProvider {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            containerColor = AlmostBlackBg,
            bottomBar = {
                // Show bottom bar only on top-level tabs
                val isTopLevel = destinations.any { it.route == currentRoute }
                if (isTopLevel) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceNavy)
                            .border(1.dp, BorderSubtle)
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            destinations.forEach { dest ->
                                val isSelected = currentRoute == dest.route
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { currentRoute = dest.route }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("nav_tab_${dest.route}")
                                ) {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = dest.titleKu,
                                        tint = if (isSelected) NeonElectricBlue else TextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = dest.titleKu,
                                        color = if (isSelected) NeonElectricBlue else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    "home" -> HomeScreen(
                        repository = repository,
                        onNavigateToChatWithPrompt = { prompt, cap ->
                            chatInitialPrompt = prompt
                            chatExplicitCapability = cap
                            currentRoute = "chat"
                        },
                        onNavigateToTool = { toolRoute ->
                            currentRoute = toolRoute
                        },
                        onOpenProfile = { currentRoute = "settings" },
                        onOpenSettings = { currentRoute = "settings" }
                    )

                    "chat" -> ChatScreen(
                        repository = repository,
                        initialPrompt = chatInitialPrompt,
                        explicitCapability = chatExplicitCapability,
                        onOpenVoice = { currentRoute = "voice" }
                    )

                    "tools" -> ToolsScreen(
                        onOpenToolScreen = { toolRoute ->
                            currentRoute = toolRoute
                        }
                    )

                    "history" -> HistoryScreen(
                        repository = repository,
                        onSelectConversation = { convId ->
                            repository.selectConversation(convId)
                            currentRoute = "chat"
                        },
                        onUsePrompt = { pText ->
                            chatInitialPrompt = pText
                            currentRoute = "chat"
                        }
                    )

                    "settings" -> SettingsScreen(
                        repository = repository,
                        onNavigateToSubScreen = { subRoute ->
                            currentRoute = subRoute
                        }
                    )

                    // Sub-screens & Studios
                    "image_studio" -> ImageStudioScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "home" }
                    )

                    "video_studio" -> VideoStudioScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "home" }
                    )

                    "voice" -> VoiceScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "home" }
                    )

                    "ai_comparison" -> AiComparisonScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "providers_manager" -> ProviderManagerScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "api_keys_manager" -> ProviderManagerScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "models_manager" -> ModelManagerScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "router_settings", "router_playground" -> RouterPlaygroundScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "usage_tracking" -> UsageAnalyticsScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "memory" -> MemoryScreen(
                        repository = repository,
                        onBackClick = { currentRoute = "settings" }
                    )

                    "coding", "writing", "translation", "files", "web_search", "study" -> {
                        // Directly open Chat with targeted capability preset
                        val cap = when (currentRoute) {
                            "coding" -> AICapability.CODING
                            "translation" -> AICapability.TRANSLATION
                            "files" -> AICapability.FILE_ANALYSIS
                            "web_search" -> AICapability.WEB_SEARCH
                            "study" -> AICapability.REASONING
                            else -> AICapability.CHAT
                        }
                        ChatScreen(
                            repository = repository,
                            initialPrompt = "",
                            explicitCapability = cap,
                            onOpenVoice = { currentRoute = "voice" }
                        )
                    }

                    else -> HomeScreen(
                        repository = repository,
                        onNavigateToChatWithPrompt = { prompt, cap ->
                            chatInitialPrompt = prompt
                            chatExplicitCapability = cap
                            currentRoute = "chat"
                        },
                        onNavigateToTool = { toolRoute -> currentRoute = toolRoute },
                        onOpenProfile = { currentRoute = "settings" },
                        onOpenSettings = { currentRoute = "settings" }
                    )
                }
            }
        }
    }
}
