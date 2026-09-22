package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BasokaRepository
import com.example.model.AIProvider
import com.example.ui.components.AddApiKeyDialog
import com.example.ui.components.GlowCard
import com.example.ui.components.KurdishTopHeader
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AlmostBlackBg
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NeonElectricBlue
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProviderManagerScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val providers by repository.providers.collectAsState()
    var showAddKeyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "بەڕێوەبردنی دابینکەرەکان (AI Providers)",
            subtitle = "کۆنترۆڵکردنی باری چالاکی، کلیل و لەپێشینەیی",
            onBackClick = onBackClick,
            trailingContent = {
                NeonButton(
                    text = "کلیل +",
                    onClick = { showAddKeyDialog = true },
                    modifier = Modifier.testTag("add_key_header_btn")
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(providers) { provider ->
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = provider.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ڕێکخستن: ${if (provider.hasApiKey || provider.isOfficialServerKey) "کلیل دانراوە" else "کلیل پێویستە"}",
                                    color = if (provider.hasApiKey || provider.isOfficialServerKey) NeonElectricBlue else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            StatusBadge(status = provider.status)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = provider.descriptionKu,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "لەپێشینەیی: ${provider.priority} • خێرایی: ~80ms",
                                color = NeonYellow,
                                fontSize = 11.sp
                            )

                            NeonButton(
                                text = if (provider.hasApiKey) "دەستکاری کلیل" else "دانانی کلیل",
                                onClick = { showAddKeyDialog = true },
                                isSecondary = true
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddKeyDialog) {
        AddApiKeyDialog(
            providers = providers,
            onSave = { providerId, apiKey, _ ->
                repository.updateProviderApiKey(providerId, apiKey)
                Toast.makeText(context, "کلیل بە سەرکەوتوویی پاشەکەوتکرا!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showAddKeyDialog = false }
        )
    }
}
