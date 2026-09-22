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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.NeonButton
import com.example.ui.theme.AlmostBlackBg
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

data class ComparisonResult(
    val modelId: String,
    val modelName: String,
    val providerName: String,
    val response: String,
    val latencyMs: Long,
    val tokens: Int,
    val costCredits: Int
)

@Composable
fun AiComparisonScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allModels by repository.models.collectAsState()

    var prompt by remember { mutableStateOf("جیاوازی نێوان ژیری دەستکردی دروستکەر (Generative AI) و ژیری دەستکردی تەقلیدی چییە؟") }
    val selectedModelIds = remember { mutableStateListOf("gemini-3.5-flash", "claude-3-5-sonnet", "deepseek-chat") }
    var isRunning by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<ComparisonResult>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "بەراوردکردنی مۆدێلەکان",
            subtitle = "ناردنی یەک پرسیار بۆ چەندین مۆدێل هاوکات",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Text(text = "پرسیار یان داواکاری هاوبەش", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("compare_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonElectricBlue,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceNavy,
                        unfocusedContainerColor = SurfaceNavy
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Model Selection Pills (Pick 2 or 3)
            item {
                Text(text = "مۆدێلەکان هەڵبژێرە بۆ بەراوردکردن (٢ یان ٣ مۆدێل):", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(allModels.take(6)) { model ->
                        val isSelected = selectedModelIds.contains(model.id)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonElectricBlue else SurfaceElevated)
                                .border(1.dp, if (isSelected) NeonElectricBlue else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (isSelected) {
                                        if (selectedModelIds.size > 1) selectedModelIds.remove(model.id)
                                    } else {
                                        if (selectedModelIds.size < 3) selectedModelIds.add(model.id)
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = model.name,
                                color = if (isSelected) AlmostBlackBg else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Run button
            item {
                NeonButton(
                    text = if (isRunning) "پشکنین بەڕێوەدەچێت..." else "دەستپێکردنی بەراوردکردن ⚡",
                    enabled = !isRunning && prompt.isNotBlank(),
                    onClick = {
                        isRunning = true
                        results.clear()
                        coroutineScope.launch {
                            delay(1200)
                            selectedModelIds.forEachIndexed { i, mId ->
                                val model = allModels.find { it.id == mId } ?: allModels.first()
                                val fakeLatency = (80 + i * 45).toLong()
                                results.add(
                                    ComparisonResult(
                                        modelId = model.id,
                                        modelName = model.name,
                                        providerName = model.providerId,
                                        response = "وەڵامی [${model.name}]:\nژیری دەستکردی دروستکەر (Generative AI) توانای بەرهەمهێنانی ناوەڕۆکی نوێی هەیە (وەک دەق و وێنە)، بەڵام تەقلیدی زیاتر بۆ دەستنیشانکردن و پۆلێنکردنە.",
                                        latencyMs = fakeLatency,
                                        tokens = 145 + i * 20,
                                        costCredits = model.creditCost
                                    )
                                )
                            }
                            isRunning = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("run_comparison_btn")
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (isRunning) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = NeonElectricBlue)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "وەڵامەکان وەردەگیرێن لە مۆدێلە جیاوازەکان...", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Results List
            items(results) { res ->
                GlowCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), hasGlow = true) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = res.modelName, color = NeonElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "${res.latencyMs}ms ⚡", color = NeonYellow, fontSize = 11.sp)
                                Text(text = "${res.tokens} Tokens", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = res.response, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
