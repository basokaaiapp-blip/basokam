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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.UserMemoryItem
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

@Composable
fun MemoryScreen(
    repository: BasokaRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val memoryItems by repository.userMemory.collectAsState()
    val isMemoryEnabled by repository.memoryEnabled.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("گشتی") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlmostBlackBg)
    ) {
        KurdishTopHeader(
            title = "بیرگەی زیرەک (AI Memory)",
            subtitle = "تایبەتمەندی و زانیارییە خەزنکراوەکان بۆ کەسییکردنی وەڵام",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Memory Master Switch
            item {
                GlowCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "چالاککردنی بیرگە (Memory Retention)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "مۆدێلەکان ڕەچاوی زانیاری و شێوازی دڵخوازی تۆ دەکەن لە کاتی گفتوگۆدا.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Switch(
                            checked = isMemoryEnabled,
                            onCheckedChange = { repository.setMemoryEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AlmostBlackBg,
                                checkedTrackColor = NeonElectricBlue,
                                uncheckedTrackColor = SurfaceElevated
                            ),
                            modifier = Modifier.testTag("memory_enable_switch")
                        )
                    }
                }
            }

            // Action Bar: Add new memory
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "زانیارییە تۆمارکراوەکان (${memoryItems.size})",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    NeonButton(
                        text = "زیادکردنی زانیاری +",
                        onClick = { showAddDialog = true },
                        isSecondary = true,
                        modifier = Modifier.testTag("add_memory_btn")
                    )
                }
            }

            if (memoryItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "هیچ زانیارییەک لە بیرگەدا تۆمار نەکراوە.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(memoryItems) { item ->
                    GlowCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.key,
                                        color = NeonElectricBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SurfaceElevated)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = item.category, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.value,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    repository.removeMemoryItem(item.id)
                                    Toast.makeText(context, "سڕایەوە", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp).testTag("delete_memory_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SurfaceNavy,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(text = "زیادکردنی زانیاری نوێ بۆ بیرگە", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        label = { Text("ناوی تایبەتمەندی (کلیل)") },
                        placeholder = { Text("وەک: شێوازی کۆدنووسین") },
                        modifier = Modifier.fillMaxWidth().testTag("mem_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonElectricBlue,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        label = { Text("ناوەڕۆک و بەها") },
                        placeholder = { Text("وەک: بە کورتکراوەیی و بە زمانی کوردی") },
                        modifier = Modifier.fillMaxWidth().testTag("mem_val_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonElectricBlue,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                NeonButton(
                    text = "پاشەکەوتکردن",
                    enabled = newKey.isNotBlank() && newValue.isNotBlank(),
                    onClick = {
                        repository.addMemoryItem(newKey.trim(), newValue.trim(), newCategory)
                        showAddDialog = false
                        newKey = ""
                        newValue = ""
                        Toast.makeText(context, "زانیاری لە بیرگەدا تۆمارکرا", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("save_memory_btn")
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "پاشگەزبوونەوە", color = TextMuted)
                }
            }
        )
    }
}
