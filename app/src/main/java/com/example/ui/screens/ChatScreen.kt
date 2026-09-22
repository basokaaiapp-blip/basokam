package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.components.ChatBubble
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.ModelSelectorDialog
import com.example.ui.components.PaidActionConfirmationDialog
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
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    repository: BasokaRepository,
    initialPrompt: String = "",
    explicitCapability: AICapability? = null,
    onOpenVoice: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val activeConvId by repository.activeConversationId.collectAsState()
    val allMessagesMap by repository.messages.collectAsState()
    val models by repository.models.collectAsState()
    val wallet by repository.wallet.collectAsState()

    val currentMessages = allMessagesMap[activeConvId] ?: emptyList()
    val listState = rememberLazyListState()

    var textInput by remember { mutableStateOf(initialPrompt) }
    var selectedModelId by remember { mutableStateOf("AUTO") }
    var showModelDialog by remember { mutableStateOf(false) }
    var pendingPaidPrompt by remember { mutableStateOf<String?>(null) }
    var showPaidDialog by remember { mutableStateOf(false) }
    var activeCapability by remember { mutableStateOf(explicitCapability) }

    LaunchedEffect(currentMessages.size) {
        if (currentMessages.isNotEmpty()) {
            listState.animateScrollToItem(currentMessages.size - 1)
        }
    }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            textInput = initialPrompt
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        // Chat Header
        KurdishTopHeader(
            title = "Basoka AI",
            subtitle = "یاریدەدەری زیرەکی سەردەمیانە",
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Credit indicator
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${wallet.balance}",
                            color = NeonYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // New Chat Button
                    IconButton(
                        onClick = { repository.createNewConversation() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("new_chat_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Chat",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        ) {
            items(currentMessages) { message ->
                ChatBubble(
                    message = message,
                    onRetry = {
                        coroutineScope.launch {
                            repository.sendMessage(
                                conversationId = activeConvId,
                                content = "دوبارە وەڵام بدەرەوە تکایە.",
                                explicitCapability = activeCapability,
                                specificModelId = selectedModelId
                            )
                        }
                    }
                )
            }
        }

        // Active Capability indicator if set
        if (activeCapability != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "توانای دیاریکراو: ${activeCapability?.kurdishName}",
                    color = NeonYellow,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "لابردن ✕",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { activeCapability = null }
                )
            }
        }

        // Chat Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceNavy)
                .border(1.dp, BorderSubtle)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic / Voice Button
                IconButton(
                    onClick = onOpenVoice,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .testTag("chat_voice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice",
                        tint = NeonYellow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = "پەیامەکەت بنووسە...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("chat_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonElectricBlue,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        val toSend = textInput.trim()
                        if (toSend.isNotBlank()) {
                            textInput = ""

                            // Check router credits
                            val decision = repository.routerEngine.route(
                                prompt = toSend,
                                explicitCapability = activeCapability,
                                mode = repository.routerMode.value,
                                isFreeOnly = repository.isFreeOnly.value,
                                specificModelId = selectedModelId
                            )

                            val cost = decision.estimatedCredits
                            if (cost > 0 && !wallet.autoApprovePaid && !decision.isFree) {
                                pendingPaidPrompt = toSend
                                showPaidDialog = true
                            } else {
                                coroutineScope.launch {
                                    repository.sendMessage(
                                        conversationId = activeConvId,
                                        content = toSend,
                                        explicitCapability = activeCapability,
                                        specificModelId = selectedModelId
                                    )
                                }
                            }
                        }
                    },
                    enabled = textInput.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (textInput.isNotBlank()) NeonElectricBlue else SurfaceElevated)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (textInput.isNotBlank()) AlmostBlackBg else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Model Selector Dialog
    if (showModelDialog) {
        ModelSelectorDialog(
            models = models,
            currentSelectedId = selectedModelId,
            onSelect = { selectedModelId = it },
            onDismiss = { showModelDialog = false }
        )
    }

    // Paid Action Confirmation Dialog
    if (showPaidDialog && pendingPaidPrompt != null) {
        val prompt = pendingPaidPrompt ?: ""
        PaidActionConfirmationDialog(
            estimatedCredits = 2,
            freeAlternativeName = "Gemini 3.5 Flash",
            onConfirm = {
                showPaidDialog = false
                coroutineScope.launch {
                    repository.sendMessage(
                        conversationId = activeConvId,
                        content = prompt,
                        explicitCapability = activeCapability,
                        specificModelId = selectedModelId
                    )
                }
                pendingPaidPrompt = null
            },
            onChooseFree = {
                showPaidDialog = false
                selectedModelId = "gemini-3.5-flash"
                coroutineScope.launch {
                    repository.sendMessage(
                        conversationId = activeConvId,
                        content = prompt,
                        explicitCapability = activeCapability,
                        specificModelId = "gemini-3.5-flash"
                    )
                }
                pendingPaidPrompt = null
            },
            onDismiss = {
                showPaidDialog = false
                pendingPaidPrompt = null
            }
        )
    }
}
