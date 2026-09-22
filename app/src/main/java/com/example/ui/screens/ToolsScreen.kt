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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.AICapability
import com.example.model.ToolItem
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

@Composable
fun ToolsScreen(
    onOpenToolScreen: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("هەمووی") }

    val categories = listOf(
        "هەمووی", "وێنە", "ڤیدیۆ", "دەنگ", "فایل", "کۆد", "نووسین", "خوێندن", "وەرگێڕان", "توێژینەوە"
    )

    val tools = remember {
        listOf(
            ToolItem("t_img_gen", "دروستکردنی وێنەی AI", "بەرهەمهێنانی وێنەی فۆتۆریالیستیک، ئەنیمێ یان 3D بە تێکست", "وێنە", AICapability.IMAGE_GENERATION, "🎨", "پێشکەوتوو"),
            ToolItem("t_img_enh", "چاککردنی کواڵێتی وێنە", "ڕوونکردنەوەی وێنەی تاریک و بەرزکردنەوەی کوالێتی بۆ 4K", "وێنە", AICapability.IMAGE_ENHANCEMENT, "✨"),
            ToolItem("t_img_bg", "لابردنی باکگراوندی وێنە", "لابردنی پاشبنەما بە شێوەیەکی خۆکار و خاوێن بە AI", "وێنە", AICapability.IMAGE_EDITING, "✂️"),
            ToolItem("t_vid_gen", "دروستکردنی ڤیدیۆی سینەمایی", "بەرهەمهێنانی ڤیدیۆی کورت بە مۆدێلی Veo و ڕێنمایی دەقی", "ڤیدیۆ", AICapability.VIDEO_GENERATION, "🎬", "نوێ"),
            ToolItem("t_vid_enh", "ئامرازەکانی ڤیدیۆ", "گەورەکردنی ڕوونی ڤیدیۆ و دروستکردنی ژێرنووس بە کوردی", "ڤیدیۆ", AICapability.VIDEO_EDITING, "📽️"),
            ToolItem("t_voice_chat", "وتووێژی دەنگی ڕاستەوخۆ", "گفتوگۆکردن بە دەنگ بەبێ پێویستی بە تایپکردن", "دەنگ", AICapability.TEXT_TO_SPEECH, "🎙️"),
            ToolItem("t_voice_stt", "گۆڕینی دەنگ بۆ دەق", "دەرهێنانی دەقی کوردی و ئینگلیزی لە تۆماری دەنگ", "دەنگ", AICapability.SPEECH_TO_TEXT, "📝"),
            ToolItem("t_pdf_anal", "شیکاری پەرتووک و PDF", "پوختەکردن، پرسیارکردن و بەدەستهێنانی زانیاری لە فایلی گەورە", "فایل", AICapability.PDF_ANALYSIS, "📚"),
            ToolItem("t_file_anal", "دەرهێنانی داتای فایل", "شیکاری فایلەکانی CSV, JSON, TXT بە شێوەی خشتە", "فایل", AICapability.FILE_ANALYSIS, "📊"),
            ToolItem("t_code_gen", "پڕۆگرامسازی و دروستکردنی کۆد", "دروستکردنی ئاپ، سایت، داتابەیس بە هەموو زمانەکان", "کۆد", AICapability.CODING, "💻", "خۆڕایی"),
            ToolItem("t_code_fix", "شیکارکردنی هەڵەی کۆد (Bug)", "دۆزینەوەی هەڵە و چاککردن بە ڕوونکردنەوەی تەواو", "کۆد", AICapability.CODING, "🐞"),
            ToolItem("t_write_rewr", "دەستکاریکردنی نووسین", "نووسینەوەی دەق بە شێوازی فەرمی یان بازاڕی", "نووسین", AICapability.CHAT, "✍️"),
            ToolItem("t_write_email", "نووسینی ئیمەیڵ و نامەی فەرمی", "داڕشتنی نامەی پرۆفیشناڵ بۆ داواکاری کار و ڕێکخراوەکان", "نووسین", AICapability.CHAT, "✉️"),
            ToolItem("t_study_quiz", "دروستکەری پرسیار و تاقیکردنەوە", "دروستکردنی پرسیاری هەمەجۆر لە وانەکان بۆ تاقیکردنەوە", "خوێندن", AICapability.REASONING, "🎓"),
            ToolItem("t_study_exp", "شیکردنەوەی وانە هەنگاو بە هەنگاو", "ڕوونکردنەوەی بابەتی ئاڵۆزی بیرکاری و فیزیا", "خوێندن", AICapability.REASONING, "📖"),
            ToolItem("t_trans_all", "وەرگێڕی زیرەکی سۆرانی", "وەرگێڕان بە تێگەیشتن لە چەمک نەک وشە بە وشە", "وەرگێڕان", AICapability.TRANSLATION, "🌍"),
            ToolItem("t_web_res", "توێژینەوەی قووڵی سەرچاوەدار", "کۆکردنەوەی نوێترین سەرچاوە و بەستەرەکان لە ئینتەرنێت", "توێژینەوە", AICapability.RESEARCH, "🔍", "سەرچاوەدار")
        )
    }

    val filteredTools = if (selectedCategory == "هەمووی") {
        tools
    } else {
        tools.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "ئامرازەکانی AI",
            subtitle = "کۆکراوەی هەموو ئامرازە زیرەکەکان لە یەک شوێن"
        )

        // Categories filter tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) NeonElectricBlue else SurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) NeonElectricBlue else BorderSubtle,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("cat_tab_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) AlmostBlackBg else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Tools List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredTools) { tool ->
                GlowCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val route = when (tool.category) {
                            "وێنە" -> "image_studio"
                            "ڤیدیۆ" -> "video_studio"
                            "دەنگ" -> "voice"
                            "فایل" -> "files"
                            "توێژینەوە" -> "web_search"
                            "خوێندن" -> "study"
                            "کۆد" -> "coding"
                            "نووسین" -> "writing"
                            "وەرگێڕان" -> "translation"
                            else -> "chat"
                        }
                        onOpenToolScreen(route)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = tool.iconEmoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tool.kurdishTitle,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (tool.badge != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonYellow.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tool.badge,
                                            color = NeonYellow,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = tool.kurdishDesc,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        NeonButton(
                            text = "کردنەوە",
                            onClick = {
                                val route = when (tool.category) {
                                    "وێنە" -> "image_studio"
                                    "ڤیدیۆ" -> "video_studio"
                                    "دەنگ" -> "voice"
                                    "فایل" -> "files"
                                    "توێژینەوە" -> "web_search"
                                    "خوێندن" -> "study"
                                    "کۆد" -> "coding"
                                    "نووسین" -> "writing"
                                    "وەرگێڕان" -> "translation"
                                    else -> "chat"
                                }
                                onOpenToolScreen(route)
                            },
                            isSecondary = true
                        )
                    }
                }
            }
        }
    }
}
