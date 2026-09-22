package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.model.VideoResult
import com.example.model.VideoStudioState
import com.example.service.VideoGenerationService
import com.example.ui.components.BasokaVideoPlayer
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.NeonButton
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun VideoStudioScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("فڕینی باڵندەیەک بەسەر دارستانەکانی زاگرۆس بە کواڵێتی 4K و ڕووناکی بەیانی") }
    var selectedRatio by remember { mutableStateOf("16:9") }
    var selectedDuration by remember { mutableStateOf("5 چرکە") }
    var selectedMotion by remember { mutableStateOf("Cinematic Pan") }
    var uiState by remember { mutableStateOf<VideoStudioState>(VideoStudioState.Idle) }

    val jobs by repository.jobs.collectAsState()

    val aspectRatios = listOf("16:9", "9:16", "1:1", "4:3")
    val durations = listOf("5 چرکە", "8 چرکە")
    val motions = listOf("Cinematic Pan", "Drone Shot", "Slow Zoom", "Orbit 360", "Dynamic Action")

    val quickPrompts = listOf(
        "دیمەنی شەپۆلی ئاو لە کەناری دەریاچەی دووکان بە شەو",
        "جوڵەی کەشتییەکی ئاسمانی لە ناو بۆشایی ئاسمان",
        "شەمەندەفەرێکی خێرا بە نێوان کێوە سەوزەکاندا تێدەپەڕێت"
    )

    fun startVideoGeneration() {
        if (prompt.isBlank()) {
            Toast.makeText(context, "تکایە دەقێک بۆ ڤیدیۆکە بنووسە.", Toast.LENGTH_SHORT).show()
            return
        }

        uiState = VideoStudioState.Starting("دەستپێکردنی کاری ڤیدیۆ...")
        coroutineScope.launch {
            val durationSec = if (selectedDuration.contains("8")) 8 else 5
            val enrichedPrompt = "$prompt, camera movement: $selectedMotion, cinematic 4k"

            val result = VideoGenerationService.generateVideo(
                context = context,
                prompt = enrichedPrompt,
                aspectRatio = selectedRatio,
                durationSec = durationSec,
                onStateChanged = { newState ->
                    uiState = newState
                }
            )

            result.fold(
                onSuccess = { videoResult ->
                    repository.registerCompletedVideoJob(videoResult)
                    Toast.makeText(context, "ڤیدیۆکە بە سەرکەوتوویی بەرهەمهات!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { err ->
                    val errorMsg = err.localizedMessage ?: "دروستکردنی ڤیدیۆ سەرکەوتوو نەبوو."
                    uiState = VideoStudioState.Error(errorMsg, canRetry = true)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "ستۆدیۆی ڤیدیۆی AI",
            subtitle = "بەرهەمهێنانی ڤیدیۆی سینەمایی ڕاستەقینە لە دەق",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp)
        ) {
            // Prompt Input
            item {
                Text(
                    text = "ڕێنمایی دەقی ڤیدیۆ (Video Prompt)",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("جوڵە، کامێرا و ڕووداوەکانی ڤیدیۆکە بنووسە...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("video_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonElectricBlue,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceNavy,
                        unfocusedContainerColor = SurfaceNavy
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Prompt Inspiration Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickPrompts) { qp ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                                .clickable { prompt = qp }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = qp.take(28) + "...",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Aspect Ratio Selector
            item {
                Text(text = "ڕەهەندی ڤیدیۆ (Aspect Ratio)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    aspectRatios.forEach { ratio ->
                        val isSel = ratio == selectedRatio
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonElectricBlue else SurfaceElevated)
                                .border(1.dp, if (isSel) NeonElectricBlue else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedRatio = ratio }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("video_ratio_btn_$ratio")
                        ) {
                            Text(
                                text = ratio,
                                color = if (isSel) AlmostBlackBg else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Duration & Camera Motion
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Duration
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ماوەی ڤیدیۆ", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            durations.forEach { dur ->
                                val isSel = dur == selectedDuration
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) NeonElectricBlue else SurfaceElevated)
                                        .border(1.dp, if (isSel) NeonElectricBlue else BorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { selectedDuration = dur }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = dur,
                                        color = if (isSel) AlmostBlackBg else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Text(text = "جوڵەی کامێرا (Camera Motion)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(motions) { motion ->
                        val isSel = motion == selectedMotion
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonYellow else SurfaceElevated)
                                .border(1.dp, if (isSel) NeonYellow else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedMotion = motion }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = motion,
                                color = if (isSel) AlmostBlackBg else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Generate Button
            item {
                val isBusy = uiState is VideoStudioState.Starting ||
                        uiState is VideoStudioState.Generating ||
                        uiState is VideoStudioState.Processing ||
                        uiState is VideoStudioState.Downloading

                NeonButton(
                    text = if (isBusy) "ڤیدیۆکە ئامادە دەکرێت..." else "دەستپێکردنی دروستکردنی ڤیدیۆ (15 Credit)",
                    enabled = !isBusy && prompt.isNotBlank(),
                    onClick = { startVideoGeneration() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_video_job_btn")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Real Video Playback Area
            item {
                val ratioFloat = when (selectedRatio) {
                    "1:1" -> 1f
                    "16:9" -> 16f / 9f
                    "9:16" -> 9f / 16f
                    "4:3" -> 4f / 3f
                    else -> 16f / 9f
                }

                when (val state = uiState) {
                    is VideoStudioState.Idle -> {
                        GlowCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratioFloat),
                            hasGlow = false
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🎬", fontSize = 44.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "ڤیدیۆی دروستکراو بە تەواوی کۆنتڕۆڵەوە لێرە پەخش دەکرێت",
                                        color = TextMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    is VideoStudioState.Starting,
                    is VideoStudioState.Generating,
                    is VideoStudioState.Processing,
                    is VideoStudioState.Downloading -> {
                        val message: String
                        val progress: Float
                        when (state) {
                            is VideoStudioState.Starting -> {
                                message = state.messageKu
                                progress = 0.1f
                            }
                            is VideoStudioState.Generating -> {
                                message = state.messageKu
                                progress = 0.3f
                            }
                            is VideoStudioState.Processing -> {
                                message = state.messageKu
                                progress = state.progress
                            }
                            is VideoStudioState.Downloading -> {
                                message = state.messageKu
                                progress = state.progress
                            }
                            else -> {
                                message = "خەریکی ئامادەکردنین..."
                                progress = 0.5f
                            }
                        }

                        GlowCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratioFloat),
                            hasGlow = true
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = NeonElectricBlue,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = message,
                                        color = NeonElectricBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        color = NeonElectricBlue,
                                        trackColor = SurfaceElevated,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "ڕەهەند: $selectedRatio • شێواز: $selectedMotion",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    is VideoStudioState.Ready -> {
                        val videoResult = state.videoResult
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // The Real Video Player Component
                            BasokaVideoPlayer(
                                videoUri = videoResult.uri,
                                videoPath = videoResult.localFilePath,
                                aspectRatioString = videoResult.aspectRatio,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Video Actions: Save, Share, Copy Prompt, Regenerate
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save to Device
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable {
                                            coroutineScope.launch {
                                                val saved = VideoGenerationService.saveToMovies(context, videoResult)
                                                if (saved) {
                                                    Toast.makeText(context, "ڤیدیۆکە لە یادگەدا پاشەکەوتکرا!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "پاشەکەوتکردنی ڤیدیۆ سەرکەوتوو نەبوو.", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("save_video_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Download, contentDescription = "Save", tint = NeonElectricBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("داگرتن", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Share
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable {
                                            VideoGenerationService.shareVideo(context, videoResult)
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("share_video_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = NeonYellow, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("هاوبەشکردن", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Copy Prompt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", videoResult.prompt))
                                            Toast.makeText(context, "ڕێنماییەکە لەبەرگیرایەوە!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("copy_video_prompt_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("کۆپی", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Regenerate
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable { startVideoGeneration() }
                                        .padding(vertical = 10.dp)
                                        .testTag("regenerate_video_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Regen", tint = NeonElectricBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("دووبارە", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    is VideoStudioState.Error -> {
                        GlowCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratioFloat),
                            hasGlow = false
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SurfaceNavy)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = state.errorMessageKu,
                                        color = ErrorRed,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (state.canRetry) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        NeonButton(
                                            text = "دووبارە هەوڵبدەوە 🔄",
                                            onClick = { startVideoGeneration() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
