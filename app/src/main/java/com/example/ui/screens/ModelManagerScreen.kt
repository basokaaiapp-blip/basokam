package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.AICapability
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ModelManagerScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val models by repository.models.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "بەڕێوەبردنی Model ـەکان",
            subtitle = "کۆنترۆڵکردنی کواڵێتی، خێرایی و خەرجی مۆدێلەکان",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(models) { model ->
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = model.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "دابینکەر: ${model.providerId}",
                                    color = NeonElectricBlue,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (model.isFree) NeonYellow.copy(alpha = 0.2f) else SurfaceElevated)
                                    .border(1.dp, if (model.isFree) NeonYellow else BorderSubtle, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (model.isFree) "خۆڕایی" else "${model.creditCost} Cr",
                                    color = if (model.isFree) NeonYellow else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = model.descriptionKu,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Capabilities Pills
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(model.capabilities.toList()) { cap ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = cap.kurdishName, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "کواڵێتی: ${"★".repeat(model.quality)}", color = NeonYellow, fontSize = 11.sp)
                            Text(text = "خێرایی: ${"⚡".repeat(model.speed)}", color = NeonElectricBlue, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
