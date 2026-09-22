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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun SettingsScreen(
    repository: BasokaRepository,
    onNavigateToSubScreen: (String) -> Unit
) {
    val wallet by repository.wallet.collectAsState()
    val isFreeOnly by repository.isFreeOnly.collectAsState()
    val isAdvancedMode by repository.isAdvancedMode.collectAsState()
    val memoryEnabled by repository.memoryEnabled.collectAsState()
    val routerMode by repository.routerMode.collectAsState()
    val stats by repository.usageTracker.stats.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            KurdishTopHeader(
                title = "ڕێکخستنەکان",
                subtitle = "بەڕێوەبردنی تەواوی پلاتفۆرمی Basoka AI"
            )
        }

        // Section: Wallet & Credits
        item {
            GlowCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                hasGlow = true
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "باڵانسی کرێدت (Wallet)", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${wallet.balance} Credit",
                                color = NeonYellow,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonElectricBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "ڕێژەی ڕۆژانە: ${wallet.dailyUsed}/${wallet.dailyLimit}", color = NeonElectricBlue, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "تەنها AI ـی خۆڕایی (Free Mode)", color = TextPrimary, fontSize = 13.sp)
                        Switch(
                            checked = isFreeOnly,
                            onCheckedChange = { repository.setFreeOnly(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AlmostBlackBg,
                                checkedTrackColor = NeonElectricBlue,
                                uncheckedTrackColor = SurfaceElevated
                            ),
                            modifier = Modifier.testTag("free_mode_switch")
                        )
                    }
                }
            }
        }

        // Section: AI Engines & Management
        item {
            Text(
                text = "بەڕێوەبردنی ژیری دەستکرد",
                color = NeonElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            SettingsNavItem("بەڕێوەبردنی دابینکەرەکان (AI Providers)", "دۆخی Google Gemini, OpenAI, Claude, DeepSeek", "🏢") {
                onNavigateToSubScreen("providers_manager")
            }
        }

        item {
            SettingsNavItem("کلیلەکانی API (API Keys)", "زیادکردن و تاقیکردنەوەی کلیلی تایبەت", "🔑") {
                onNavigateToSubScreen("api_keys_manager")
            }
        }

        item {
            SettingsNavItem("بەڕێوەبردنی Model ـەکان", "پێڕستی مۆدێلەکان، خێرایی، کوالێتی و نرخی کرێدت", "🧠") {
                onNavigateToSubScreen("models_manager")
            }
        }

        item {
            SettingsNavItem("ڕێکخستنی Auto AI Router", "دۆخی هەڵبژاردنی خۆکار: ${routerMode.kurdishTitle}", "🧭") {
                onNavigateToSubScreen("router_settings")
            }
        }

        item {
            SettingsNavItem("بەراوردکردنی مۆدێلەکان (AI Comparison)", "تاقیکردنەوەی چەندین مۆدێل لەسەر یەک داواکاری هاوکات", "⚖️") {
                onNavigateToSubScreen("ai_comparison")
            }
        }

        item {
            SettingsNavItem("تاقیکردنەوەی Auto Router (Playground)", "شیکاری دۆزینەوەی توانست و زنجیرەی Fallback", "🧪") {
                onNavigateToSubScreen("router_playground")
            }
        }

        // Section: Preferences
        item {
            Text(
                text = "تایبەتمەندی و بەکارهێنان",
                color = NeonElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            SettingsNavItem("ئاماری بەکارهێنان (Usage Tracking)", "${stats.totalRequests} داواکاری، ${stats.totalCreditsUsed} کرێدت خەرجکراو", "📊") {
                onNavigateToSubScreen("usage_tracking")
            }
        }

        item {
            SettingsNavItem("بیرگەی تایبەتی (Memory)", if (memoryEnabled) "چالاککراوە" else "ناچالاککراوە", "💾") {
                onNavigateToSubScreen("memory")
            }
        }

        // Section: System & Appearance
        item {
            Text(
                text = "سیستەم و پاراستن",
                color = NeonElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "دۆخی پێشکەوتوو (Advanced Mode)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "پیشاندانی وردەکاری تەکنیکی مۆدێل و زنجیرەی ڕاوتەر", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isAdvancedMode,
                        onCheckedChange = { repository.setAdvancedMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AlmostBlackBg,
                            checkedTrackColor = NeonYellow,
                            uncheckedTrackColor = SurfaceElevated
                        ),
                        modifier = Modifier.testTag("advanced_mode_switch")
                    )
                }
            }
        }

        item {
            SettingsNavItem("زمان (Language)", "کوردی سۆرانی (پێشگریمانکراو)", "🌐") {}
        }

        item {
            SettingsNavItem("ڕووکاری ئەپ (Theme)", "Dark Futuristic (تاریکی نێۆن)", "🌙") {}
        }

        item {
            SettingsNavItem("دەربارەی Basoka AI", "وەشانی 1.0.0 • دروستکراو بە تەلارسازی فرە-AI", "ℹ️") {}
        }
    }
}

@Composable
fun SettingsNavItem(
    title: String,
    subtitle: String,
    emoji: String,
    onClick: () -> Unit
) {
    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Text(text = "←", color = NeonElectricBlue, fontSize = 16.sp)
        }
    }
}
