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
import com.example.model.RouterMode
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.NeonButton
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

@Composable
fun RouterPlaygroundScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    var testPrompt by remember { mutableStateOf("وێنەیەکی کێوی پیرەمەگروون بە شێوازی فۆتۆریالیستیک بکێشە") }
    val currentMode by repository.routerMode.collectAsState()
    val isFreeOnly by repository.isFreeOnly.collectAsState()

    var activeMode by remember { mutableStateOf(currentMode) }

    val decision = remember(testPrompt, activeMode, isFreeOnly) {
        repository.routerEngine.route(
            prompt = testPrompt,
            mode = activeMode,
            isFreeOnly = isFreeOnly
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "تاقیکردنەوەی Auto AI Router",
            subtitle = "شیکاری چۆنیەتی بڕیاردان و زنجیرەی Fallback",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Text(text = "دەقی تاقیکردنەوە (Test Prompt)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = testPrompt,
                    onValueChange = { testPrompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("router_test_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonElectricBlue,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceNavy,
                        unfocusedContainerColor = SurfaceNavy
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Router Mode selector
            item {
                Text(text = "دۆخی دیاریکراوی ڕاوتەر:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(RouterMode.values()) { mode ->
                        val isSel = mode == activeMode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NeonElectricBlue else SurfaceElevated)
                                .border(1.dp, if (isSel) NeonElectricBlue else BorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { activeMode = mode }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mode.kurdishTitle,
                                color = if (isSel) AlmostBlackBg else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Decision Results Card
            item {
                GlowCard(modifier = Modifier.fillMaxWidth(), hasGlow = true) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مۆدێلی هەڵبژێردراوی سەرەکی",
                                color = NeonElectricBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonYellow.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (decision.isFree) "خۆڕایی (0 Cr)" else "${decision.estimatedCredits} Credit",
                                    color = NeonYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${decision.selectedModel.name} (${decision.selectedProvider.name})",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "توانستی دۆزراوە: ${decision.capability.kurdishName}",
                            color = NeonElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "هۆکاری هەڵبژاردن: ${decision.reasonKurdish}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fallback Chain Card
            item {
                Text(
                    text = "زنجیرەی جێگرەوەکان (Fallback Chain)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (decision.fallbackChain.isEmpty()) {
                    Text(text = "هیچ مۆدێلێکی جێگرەوە پێویست نییە.", color = TextMuted, fontSize = 12.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        decision.fallbackChain.forEachIndexed { idx, fbModel ->
                            GlowCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "${idx + 1}.", color = NeonYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = fbModel.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(text = fbModel.descriptionKu, color = TextSecondary, fontSize = 10.sp, maxLines = 1)
                                        }
                                    }
                                    Text(
                                        text = if (fbModel.isFree) "خۆڕایی" else "${fbModel.creditCost} Cr",
                                        color = if (fbModel.isFree) NeonElectricBlue else TextMuted,
                                        fontSize = 11.sp
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
