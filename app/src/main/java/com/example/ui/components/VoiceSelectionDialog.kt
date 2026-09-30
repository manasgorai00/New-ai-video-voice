package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DubVoice
import com.example.data.SampleDataProvider
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.StudioSurfaceVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceSelectionDialog(
    initialVoiceId: String,
    initialPitch: Float,
    initialSpeed: Float,
    initialTone: String,
    detectedGender: String? = null,
    detectedAge: String? = null,
    onPreviewVoice: (voiceId: String, pitch: Float, speed: Float) -> Unit,
    onApply: (voiceId: String, pitch: Float, speed: Float, tone: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedVoiceId by remember { mutableStateOf(initialVoiceId) }
    var pitch by remember { mutableFloatStateOf(initialPitch) }
    var speed by remember { mutableFloatStateOf(initialSpeed) }
    var selectedTone by remember { mutableStateOf(initialTone) }

    val toneOptions = listOf("Natural Warm", "Studio Broadcast", "Conversational", "Deep Narrative")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = ElectricViolet,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Set Voice Mode",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Custom humane TTS voice profile & acoustic tuning",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Auto-detected info card
                if (detectedGender != null || detectedAge != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = AcousticCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Speaker Auto-Detection",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AcousticCyan
                                    )
                                    Text(
                                        "Detected: ${detectedGender ?: "Neutral"} • Age: ${detectedAge ?: "Adult"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Voice selection list
                item {
                    Text(
                        "Select Human Voice (Male / Female)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(SampleDataProvider.availableVoices) { voice ->
                    val isSelected = voice.id == selectedVoiceId
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) StudioSurfaceHover else StudioSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) ElectricViolet else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedVoiceId = voice.id
                                selectedTone = voice.toneProfile
                                pitch = voice.defaultPitch
                                speed = voice.defaultSpeed
                            }
                            .testTag("voice_item_${voice.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (voice.gender == "Male") Color(0xFF1E3A8A) else Color(0xFF831843)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (voice.gender == "Male") "♂" else "♀",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        voice.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "(${voice.gender} • ${voice.ageGroup})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AcousticCyan
                                    )
                                }
                                Text(
                                    voice.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { onPreviewVoice(voice.id, pitch, speed) },
                                modifier = Modifier.testTag("preview_voice_${voice.id}")
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Audition Voice",
                                    tint = StudioAmber
                                )
                            }
                        }
                    }
                }

                // Pitch & Speed Sliders
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Voice Pitch: ${(pitch * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = pitch,
                        onValueChange = { pitch = it },
                        valueRange = 0.5f..1.8f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricViolet,
                            activeTrackColor = ElectricViolet
                        ),
                        modifier = Modifier.testTag("voice_pitch_slider")
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Speech Speed / Cadence: ${(speed * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = AcousticCyan,
                            activeTrackColor = AcousticCyan
                        ),
                        modifier = Modifier.testTag("voice_speed_slider")
                    )
                }

                // Tone Preset Chips
                item {
                    Text(
                        "Acoustic Tone Preset",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        toneOptions.forEach { tone ->
                            val isSelected = tone == selectedTone
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedTone = tone },
                                label = { Text(tone, fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricViolet.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(selectedVoiceId, pitch, speed, selectedTone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("apply_voice_mode_button")
            ) {
                Text("Apply Voice Mode")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = StudioSurface
    )
}
