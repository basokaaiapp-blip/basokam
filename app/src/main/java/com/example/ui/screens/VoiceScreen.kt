package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.model.AICapability
import com.example.ui.components.BasokaLogo
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VoiceScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }
    var transcriptionText by remember { mutableStateOf("دەست دابگرە لەسەر مایکرۆفۆن و قسە بکە...") }
    var aiVoiceResponse by remember { mutableStateOf<String?>(null) }

    val activeConvId by repository.activeConversationId.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "وتووێژی دەنگی ڕاستەوخۆ",
            subtitle = "قسەکردن بە زمانی کوردی سۆرانی بەبێ تایپکردن",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Live Status Card
            GlowCard(
                modifier = Modifier.fillMaxWidth(),
                hasGlow = isListening
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isListening) "گوێ دەگرێت..." else "ئامادەیە بۆ قسەکردن",
                        color = if (isListening) NeonElectricBlue else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = transcriptionText,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }

            // Animated Visualizer
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(waveScale)
                            .clip(CircleShape)
                            .background(NeonElectricBlue.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(waveScale * 0.9f)
                            .clip(CircleShape)
                            .background(NeonYellow.copy(alpha = 0.2f))
                    )
                }

                BasokaLogo(size = 72.dp)
            }

            // Waveform bars simulation
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(48.dp)
            ) {
                val heights = if (isListening) listOf(24.dp, 40.dp, 16.dp, 48.dp, 32.dp, 44.dp, 20.dp)
                else listOf(8.dp, 8.dp, 8.dp, 8.dp, 8.dp, 8.dp, 8.dp)

                heights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(h)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isListening) NeonElectricBlue else SurfaceElevated)
                    )
                }
            }

            // Mic Toggle Button
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(if (isListening) NeonYellow else SurfaceNavy)
                    .border(2.dp, if (isListening) NeonYellow else BorderGlow, CircleShape)
                    .clickable {
                        isListening = !isListening
                        if (isListening) {
                            transcriptionText = "سڵاو Basoka AI، نوێترین هەواڵەکانی تەکنەلۆژیام پێ بڵێ"
                            coroutineScope.launch {
                                delay(2200)
                                isListening = false
                                transcriptionText = "نێردرا بۆ وتووێژ..."
                                repository.sendMessage(
                                    conversationId = activeConvId,
                                    content = "سڵاو Basoka AI، نوێترین هەواڵەکانی تەکنەلۆژیام پێ بڵێ",
                                    explicitCapability = AICapability.TEXT_TO_SPEECH
                                )
                                aiVoiceResponse = "وەڵام: ژیری دەستکرد لە هەموو بوارەکاندا پێشکەوتنی خێرای بەخۆیەوە بینیوە."
                            }
                        }
                    }
                    .testTag("voice_mic_toggle"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = if (isListening) AlmostBlackBg else NeonElectricBlue,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = if (isListening) "داگرە بۆ تەواوکردن" else "داگرە بۆ دەستپێکردنی گفتوگۆ",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}
