package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.BasokaRepository
import com.example.model.ImageResult
import com.example.model.ImageStudioState
import com.example.service.ImageGenerationService
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.NeonButton
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderGlow
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
import java.io.File

@Composable
fun ImageStudioScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("دیمەنێکی فۆتۆریالیستیکی کێوی هەڵگورد لە کوردستان بە بەفر و خۆرئاوابوون") }
    var selectedRatio by remember { mutableStateOf("1:1") }
    var selectedStyle by remember { mutableStateOf("Cinematic") }
    var uiState by remember { mutableStateOf<ImageStudioState>(ImageStudioState.Idle) }
    var showFullscreenDialog by remember { mutableStateOf(false) }

    val aspectRatios = listOf("1:1", "16:9", "9:16", "4:3")
    val styles = listOf("Cinematic", "Photorealistic", "Anime", "3D Render", "Cyberpunk", "Kurdish Art")

    val quickPrompts = listOf(
        "قەڵای دێرینی هەولێر لە کاتی شەودا بە ڕووناکی نەئۆن",
        "سروشتی دڵڕفێنی هاوینەهەواری بێخاڵ بە کواڵێتی 4K",
        "کەسایەتییەکی ئەفسانەیی کوردی بە ستایلی دیجیتاڵ ئارت"
    )

    fun startGeneration() {
        if (prompt.isBlank()) {
            Toast.makeText(context, "تکایە دەقێک بنووسە بۆ دروستکردنی وێنە.", Toast.LENGTH_SHORT).show()
            return
        }

        uiState = ImageStudioState.Generating("خەریکی دروستکردنی وێنەین بە کوالێتی بەرز...")
        coroutineScope.launch {
            val result = ImageGenerationService.generateImage(
                context = context,
                prompt = prompt,
                aspectRatio = selectedRatio,
                style = selectedStyle
            )

            result.fold(
                onSuccess = { imgResult ->
                    uiState = ImageStudioState.Success(imgResult)
                    repository.registerCompletedImageJob(imgResult)
                    Toast.makeText(context, "وێنەکە بە سەرکەوتوویی بەرهەمهات!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { err ->
                    val errorMsg = err.localizedMessage ?: "دروستکردنی وێنە سەرکەوتوو نەبوو."
                    uiState = ImageStudioState.Error(errorMsg, canRetry = true)
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
            title = "ستۆدیۆی وێنەی AI",
            subtitle = "دروستکردنی وێنەی ڕاستەقینە بە ژیری دەستکرد",
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
                    text = "ڕێنمایی وەسفی وێنە (Prompt)",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("وەسفی ئەو وێنەیە بکە کە دەتەوێت دروستی بکەیت...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("image_prompt_input"),
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

                // Quick Prompt Suggestions
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
                Text(text = "ڕەهەندی وێنە (Aspect Ratio)", color = TextSecondary, fontSize = 12.sp)
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
                                .testTag("ratio_btn_$ratio")
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

            // Style Presets
            item {
                Text(text = "شێواز (Style)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(styles) { style ->
                        val isSel = style == selectedStyle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonYellow else SurfaceElevated)
                                .border(1.dp, if (isSel) NeonYellow else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedStyle = style }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = style,
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
                val isBusy = uiState is ImageStudioState.Generating
                NeonButton(
                    text = if (isBusy) "دروست دەکرێت..." else "دروستکردنی وێنە (4 Credit)",
                    enabled = !isBusy && prompt.isNotBlank(),
                    onClick = { startGeneration() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_image_btn")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Render Area (Real Image Display)
            item {
                val ratioFloat = when (selectedRatio) {
                    "1:1" -> 1f
                    "16:9" -> 16f / 9f
                    "9:16" -> 9f / 16f
                    "4:3" -> 4f / 3f
                    else -> 1f
                }

                when (val state = uiState) {
                    is ImageStudioState.Idle -> {
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
                                    Text(text = "🖼️", fontSize = 44.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "وێنەی بەرهەمهاتوو لێرە دەردەکەوێت",
                                        color = TextMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    is ImageStudioState.Generating -> {
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
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = NeonElectricBlue,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = state.messageKu,
                                        color = NeonElectricBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "کوالێتی $selectedRatio • شێوازی $selectedStyle",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    is ImageStudioState.Success -> {
                        val imageResult = state.imageResult
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // The Real Image Display
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(ratioFloat)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.5.dp, BorderGlow, RoundedCornerShape(16.dp))
                                    .background(SurfaceNavy)
                                    .testTag("real_generated_image_container")
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(File(imageResult.localFilePath))
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = imageResult.prompt,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { showFullscreenDialog = true }
                                )

                                // Top Overlay: Fullscreen trigger
                                IconButton(
                                    onClick = { showFullscreenDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AlmostBlackBg.copy(alpha = 0.7f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Buttons Row: Save, Share, Copy Prompt, Regenerate
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Save to device
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable {
                                            coroutineScope.launch {
                                                val saved = ImageGenerationService.saveToGallery(context, imageResult)
                                                if (saved) {
                                                    Toast.makeText(context, "وێنەکە لە گەلەری پاشەکەوتکرا!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "پاشەکەوتکردن سەرکەوتوو نەبوو.", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("save_image_btn"),
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
                                            ImageGenerationService.shareImage(context, imageResult)
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("share_image_btn"),
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
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", imageResult.prompt))
                                            Toast.makeText(context, "ڕێنماییەکە لەبەرگیرایەوە!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("copy_image_prompt_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("کۆپیکردن", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Regenerate
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                        .clickable { startGeneration() }
                                        .padding(vertical = 10.dp)
                                        .testTag("regenerate_image_btn"),
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

                        // Fullscreen Dialog
                        if (showFullscreenDialog) {
                            Dialog(onDismissRequest = { showFullscreenDialog = false }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.92f))
                                        .clickable { showFullscreenDialog = false },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(File(imageResult.localFilePath))
                                            .build(),
                                        contentDescription = "Fullscreen",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    IconButton(
                                        onClick = { showFullscreenDialog = false },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(16.dp)
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AlmostBlackBg)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    is ImageStudioState.Error -> {
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
                                            onClick = { startGeneration() }
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
