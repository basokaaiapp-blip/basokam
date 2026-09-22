package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AIModel
import com.example.model.AIProvider
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
fun PaidActionConfirmationDialog(
    estimatedCredits: Int,
    freeAlternativeName: String?,
    onConfirm: () -> Unit,
    onChooseFree: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "پشتڕاستکردنەوەی خەرجی کرێدت",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "ئەم کارە نزیکەی $estimatedCredits Credit پێویستە.",
                    color = NeonYellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ئایا دەتەوێت بەردەوام بیت بە مۆدێلی پێشکەوتوو، یان مۆدێلێکی خۆڕایی هەڵبژێریت؟",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        },
        confirmButton = {
            NeonButton(
                text = "بەردەوام بە",
                onClick = onConfirm,
                modifier = Modifier.testTag("confirm_paid_btn")
            )
        },
        dismissButton = {
            if (freeAlternativeName != null) {
                TextButton(
                    onClick = onChooseFree,
                    modifier = Modifier.testTag("choose_free_btn")
                ) {
                    Text(
                        text = "Model ـێکی خۆڕایی هەڵبژێرە",
                        color = NeonElectricBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(text = "پاشگەزبوونەوە", color = TextMuted)
                }
            }
        }
    )
}

@Composable
fun AddApiKeyDialog(
    providers: List<AIProvider>,
    onSave: (providerId: String, apiKey: String, alias: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedProviderId by remember { mutableStateOf(providers.firstOrNull()?.id ?: "google_gemini") }
    var apiKeyText by remember { mutableStateOf("") }
    var aliasText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "زیادکردنی کلیلی API نوێ",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column {
                Text(text = "دابینکەر هەڵبژێرە:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // Provider picker
                providers.forEach { provider ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProviderId = provider.id }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedProviderId == provider.id,
                            onClick = { selectedProviderId = provider.id },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = provider.name, color = TextPrimary, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = apiKeyText,
                    onValueChange = { apiKeyText = it },
                    label = { Text("کلیلی API") },
                    placeholder = { Text("وەک: sk-... یان AIza...") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonElectricBlue,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = aliasText,
                    onValueChange = { aliasText = it },
                    label = { Text("ناوی دڵخواز (ئیختیاری)") },
                    placeholder = { Text("کلیلی تایبەتی من") },
                    modifier = Modifier.fillMaxWidth(),
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
                enabled = apiKeyText.isNotBlank(),
                onClick = {
                    onSave(selectedProviderId, apiKeyText.trim(), aliasText.trim())
                    onDismiss()
                },
                modifier = Modifier.testTag("save_api_key_btn")
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "پاشگەزبوونەوە", color = TextMuted)
            }
        }
    )
}

@Composable
fun ModelSelectorDialog(
    models: List<AIModel>,
    currentSelectedId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "هەڵبژاردنی Model",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            LazyColumn(modifier = Modifier.height(320.dp)) {
                item {
                    // AUTO option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (currentSelectedId == "AUTO") SurfaceElevated else AlmostBlackBg)
                            .clickable {
                                onSelect("AUTO")
                                onDismiss()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSelectedId == "AUTO",
                            onClick = {
                                onSelect("AUTO")
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "خۆکار (AUTO)", color = NeonElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Router بە شێوەی زیرەک باشترین مۆدێل دیاری دەکات", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(models) { model ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (currentSelectedId == model.id) SurfaceElevated else AlmostBlackBg)
                            .clickable {
                                onSelect(model.id)
                                onDismiss()
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSelectedId == model.id,
                            onClick = {
                                onSelect(model.id)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = model.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                if (model.isFree) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "خۆڕایی", color = NeonYellow, fontSize = 10.sp)
                                }
                            }
                            Text(text = model.descriptionKu, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "داخستن", color = NeonElectricBlue)
            }
        }
    )
}
