package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.model.AICapability
import com.example.ui.components.BasokaLogo
import com.example.ui.components.GlowCard
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class QuickAction(
    val titleKu: String,
    val emoji: String,
    val capability: AICapability,
    val routeKey: String
)

@Composable
fun HomeScreen(
    repository: BasokaRepository,
    onNavigateToChatWithPrompt: (String, AICapability?) -> Unit,
    onNavigateToTool: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val wallet by repository.wallet.collectAsState()
    val routerMode by repository.routerMode.collectAsState()
    val recentConversations by repository.conversations.collectAsState()
    var promptInput by remember { mutableStateOf("") }

    val quickActions = remember {
        listOf(
            QuickAction("گفتوگۆ", "💬", AICapability.CHAT, "chat"),
            QuickAction("وێنە دروست بکە", "🎨", AICapability.IMAGE_GENERATION, "image_studio"),
            QuickAction("وێنە چاک بکە", "✨", AICapability.IMAGE_ENHANCEMENT, "image_studio"),
            QuickAction("ڤیدیۆ دروست بکە", "🎬", AICapability.VIDEO_GENERATION, "video_studio"),
            QuickAction("فایل شیکەرەوە", "📄", AICapability.FILE_ANALYSIS, "files"),
            QuickAction("گەڕان لە وێب", "🌐", AICapability.WEB_SEARCH, "web_search"),
            QuickAction("وەرگێڕان", "🌍", AICapability.TRANSLATION, "translation"),
            QuickAction("نووسین", "✍️", AICapability.CHAT, "writing"),
            QuickAction("کۆد", "💻", AICapability.CODING, "coding"),
            QuickAction("دەنگ", "🎙️", AICapability.TEXT_TO_SPEECH, "voice")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasokaLogo(size = 42.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Basoka AI",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "پلاتفۆرمی فرە-AI بە کوردی",
                            color = NeonElectricBlue,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Credit Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clickable { onOpenSettings() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${wallet.balance} Cr",
                            color = NeonYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("home_profile_btn")
                    ) {
                        Text(text = "👤", fontSize = 16.sp)
                    }
                }
            }
        }

        // Kurdish Greeting Banner
        item {
            GlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                hasGlow = true
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "سڵاو 👋",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonElectricBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "دۆخ: ${routerMode.kurdishTitle}",
                                color = NeonElectricBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Basoka AI ئامادەیە بۆ یارمەتیدانت لە وتووێژ، وێنە، ڤیدیۆ، کۆد و گەڕان.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Main AI Input Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceNavy)
                    .border(1.5.dp, BorderGlow, RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                Column {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = {
                            Text(
                                text = "چی دەتەوێت بکەم؟",
                                color = TextMuted,
                                fontSize = 15.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .testTag("home_main_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Quick feature chips inside input
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceElevated)
                                    .clickable { onNavigateToTool("voice") }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = "🎙️ دەنگ", color = TextSecondary, fontSize = 11.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceElevated)
                                    .clickable { onNavigateToTool("image_studio") }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = "🎨 وێنە", color = TextSecondary, fontSize = 11.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceElevated)
                                    .clickable { onNavigateToTool("files") }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = "📎 فایل", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        IconButton(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    val text = promptInput.trim()
                                    promptInput = ""
                                    onNavigateToChatWithPrompt(text, null)
                                }
                            },
                            enabled = promptInput.isNotBlank(),
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (promptInput.isNotBlank()) NeonElectricBlue else SurfaceElevated)
                                .testTag("home_send_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (promptInput.isNotBlank()) AlmostBlackBg else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Actions Section Header
        item {
            Text(
                text = "ئامرازە خێراکان",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Quick Actions 2-Column Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickActions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { action ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceElevated)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (action.routeKey == "chat") {
                                            onNavigateToChatWithPrompt("", action.capability)
                                        } else {
                                            onNavigateToTool(action.routeKey)
                                        }
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = action.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = action.titleKu,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Recent Activity / Conversations
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "چالاکییەکانی ئەم دواییە",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "بینینی هەمووی",
                    color = NeonElectricBlue,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigateToTool("history") }
                )
            }
        }

        if (recentConversations.isEmpty()) {
            item {
                Text(
                    text = "هیچ چالاکییەکی نوێ نییە.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(recentConversations.take(3).size) { index ->
                val conv = recentConversations[index]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceNavy)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onNavigateToChatWithPrompt("", null) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = conv.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (conv.lastSnippet.isNotEmpty()) {
                                Text(
                                    text = conv.lastSnippet,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                        Text(text = "←", color = NeonElectricBlue, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
