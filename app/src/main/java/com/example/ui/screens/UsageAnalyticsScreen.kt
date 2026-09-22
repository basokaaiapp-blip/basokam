package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun UsageAnalyticsScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val stats by repository.usageTracker.stats.collectAsState()
    val records = repository.usageTracker.getAllRecords()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "ئاماری بەکارهێنان (Analytics)",
            subtitle = "چاودێری کرێدت، داواکاری و خێرایی مۆدێلەکان",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Stats 4-box grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(modifier = Modifier.weight(1f), title = "کۆی داواکاری", value = "${stats.totalRequests}", emoji = "⚡")
                    StatBox(modifier = Modifier.weight(1f), title = "کرێدتی خەرجکراو", value = "${stats.totalCreditsUsed}", emoji = "💳")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(modifier = Modifier.weight(1f), title = "وێنەی بەرهەمهاتوو", value = "${stats.totalImagesGenerated}", emoji = "🎨")
                    StatBox(modifier = Modifier.weight(1f), title = "خوولەکی دەنگی", value = "${stats.totalVoiceMinutes}m", emoji = "🎙️")
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            item {
                Text(
                    text = "تۆماری داواکارییەکان (Recent Requests Log)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(records) { record ->
                GlowCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = record.modelId, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${record.capability.kurdishName} • ${record.latencyMs}ms", color = TextSecondary, fontSize = 11.sp)
                        }
                        Text(
                            text = if (record.creditsUsed == 0) "خۆڕایی" else "-${record.creditsUsed} Cr",
                            color = if (record.creditsUsed == 0) NeonElectricBlue else NeonYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    emoji: String
) {
    GlowCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
                Text(text = emoji, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
