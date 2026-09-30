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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleDataProvider
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCoral
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import java.util.Locale

@Composable
fun DubbingStudioScreen(
    project: VideoProject?,
    cues: List<SubtitleCue>,
    isProcessingAudioToSubtitles: Boolean,
    onRunAudioToSubtitles: () -> Unit,
    onOpenVoiceModeDialog: () -> Unit,
    onOpenEditCueDialog: (SubtitleCue) -> Unit,
    onAddNewCue: () -> Unit,
    onDeleteCue: (Long) -> Unit,
    onTestDubCue: (SubtitleCue) -> Unit,
    onPreviewVoice: (voiceId: String, pitch: Float, speed: Float) -> Unit,
    onUpdateTargetLanguage: (String) -> Unit,
    onUpdateAudioMix: (origVol: Float, dubVol: Float, duck: Boolean, reverb: Boolean) -> Unit
) {
    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No Video Project Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Select or import a video in the Library tab to configure voice dubbing and cues.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var langDropdownExpanded by remember { mutableStateOf(false) }

    var origVol by remember(project.id, project.originalAudioVolume) { mutableFloatStateOf(project.originalAudioVolume) }
    var dubVol by remember(project.id, project.dubbedAudioVolume) { mutableFloatStateOf(project.dubbedAudioVolume) }
    var duckEnabled by remember(project.id, project.audioDuckEnabled) { mutableStateOf(project.audioDuckEnabled) }
    var reverbEnabled by remember(project.id, project.reverbEnabled) { mutableStateOf(project.reverbEnabled) }

    val currentLang = SampleDataProvider.getLanguageByCode(project.targetLanguage)
    val voice = SampleDataProvider.getVoiceById(project.voiceId)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Header with Target Language Switcher
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Dubbing Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                project.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = AcousticCyan,
                                maxLines = 1
                            )
                        }

                        // Target Language Selector Dropdown
                        Box {
                            OutlinedButton(
                                onClick = { langDropdownExpanded = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("target_language_selector")
                            ) {
                                Text("${currentLang.flag} ${currentLang.displayName}", fontSize = 12.sp)
                            }
                            DropdownMenu(
                                expanded = langDropdownExpanded,
                                onDismissRequest = { langDropdownExpanded = false }
                            ) {
                                SampleDataProvider.supportedLanguages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(lang.flag, fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(lang.displayName, fontWeight = FontWeight.Bold)
                                                    Text(lang.accentRegion, fontSize = 11.sp, color = Color.Gray)
                                                }
                                            }
                                        },
                                        onClick = {
                                            onUpdateTargetLanguage(lang.code)
                                            langDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Feature 1: Subtitle Extraction & "Audio to Subtitles"
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AcousticCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AcousticCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Audio to Subtitles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("On-device neural speech extraction & cues", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = onRunAudioToSubtitles,
                            enabled = !isProcessingAudioToSubtitles,
                            colors = ButtonDefaults.buttonColors(containerColor = AcousticCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("run_audio_to_subtitles_button")
                        ) {
                            if (isProcessingAudioToSubtitles) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Extract & Detect", color = Color(0xFF00363D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    if (isProcessingAudioToSubtitles) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AcousticCyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Analyzing audio frequency bands, detecting speaker gender & age, generating timestamped cues...",
                            fontSize = 11.sp,
                            color = AcousticCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-Detected Speaker Profile summary
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Detected Speakers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("2 Speakers", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Gender Profile", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Male / Female", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ElectricViolet)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Age Classification", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Adult / Young", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StudioAmber)
                            }
                        }
                    }
                }
            }
        }

        // Feature 2: Voice Customization ("Set Voice Mode")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Voice Customization", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Humane real-human acoustic profile", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = onOpenVoiceModeDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("set_voice_mode_button")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Voice Mode", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Voice Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurface)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        voice.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "(${voice.gender} • ${project.voiceTone})",
                                        fontSize = 11.sp,
                                        color = AcousticCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Pitch: ${(project.voicePitch * 100).toInt()}% • Speed: ${(project.voiceSpeed * 100).toInt()}% • Non-robotic modulation",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    onPreviewVoice(project.voiceId, project.voicePitch, project.voiceSpeed)
                                },
                                modifier = Modifier.testTag("audition_active_voice_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Listen", tint = StudioAmber)
                            }
                        }
                    }
                }
            }
        }

        // Feature 3: Audio Mixing Controls (Original Audio, Dubbed Vocals, Ducking, Reverb)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Audio Mixing & Separation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Blend original background track with dubbed speech", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Original Background Volume
                    Text("Background Audio Level: ${(origVol * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Slider(
                        value = origVol,
                        onValueChange = {
                            origVol = it
                            onUpdateAudioMix(origVol, dubVol, duckEnabled, reverbEnabled)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = AcousticCyan, activeTrackColor = AcousticCyan),
                        modifier = Modifier.testTag("mixer_orig_vol_slider")
                    )

                    // Dubbed Speech Volume
                    Text("Dubbed Speech Level: ${(dubVol * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Slider(
                        value = dubVol,
                        onValueChange = {
                            dubVol = it
                            onUpdateAudioMix(origVol, dubVol, duckEnabled, reverbEnabled)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = ElectricViolet, activeTrackColor = ElectricViolet),
                        modifier = Modifier.testTag("mixer_dub_vol_slider")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto Speech Ducking", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Softens original audio when dubbed voice speaks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = duckEnabled,
                            onCheckedChange = {
                                duckEnabled = it
                                onUpdateAudioMix(origVol, dubVol, duckEnabled, reverbEnabled)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricViolet),
                            modifier = Modifier.testTag("mixer_duck_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Light Reverb Simulation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Applies warm studio acoustic presence to vocal synthesis", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = reverbEnabled,
                            onCheckedChange = {
                                reverbEnabled = it
                                onUpdateAudioMix(origVol, dubVol, duckEnabled, reverbEnabled)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = AcousticCyan),
                            modifier = Modifier.testTag("mixer_reverb_switch")
                        )
                    }
                }
            }
        }

        // Subtitle Cues Editor Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Timestamped Cues (${cues.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Button(
                    onClick = onAddNewCue,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_cue_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Cue", fontSize = 12.sp)
                }
            }
        }

        // List of cues with audio test play and edit
        items(cues) { cue ->
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cue_item_${cue.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudioSurface)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "${formatTime(cue.startTimeMs)} - ${formatTime(cue.endTimeMs)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcousticCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                cue.speaker,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (cue.detectedGender == "Male") Color(0xFF1E3A8A) else Color(0xFF831843))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    cue.detectedGender,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row {
                            IconButton(
                                onClick = { onTestDubCue(cue) },
                                modifier = Modifier.testTag("test_dub_cue_${cue.id}")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Test Dub", tint = StudioAmber)
                            }
                            IconButton(
                                onClick = { onOpenEditCueDialog(cue) },
                                modifier = Modifier.testTag("edit_cue_${cue.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AcousticCyan)
                            }
                            IconButton(
                                onClick = { onDeleteCue(cue.id) },
                                modifier = Modifier.testTag("delete_cue_${cue.id}")
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = StudioCoral)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Dub: ${cue.translatedText.ifBlank { cue.originalText }}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        "Orig: ${cue.originalText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
