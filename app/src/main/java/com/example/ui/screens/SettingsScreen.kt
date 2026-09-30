package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import java.util.Locale

@Composable
fun SettingsScreen(
    currentProject: VideoProject?,
    cues: List<SubtitleCue>,
    engineStatus: String,
    availableVoiceCount: Int,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var exportedSrtText by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Settings & Dubbing Engine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Engine Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Neural TTS Speech Synthesizer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                engineStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = AcousticCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Installed TTS Voices",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "$availableVoiceCount system voices available",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Localized Accent Engine",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "10 Supported Locales",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioAmber
                        )
                    }
                }
            }
        }

        // Subtitle Export Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = AcousticCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Export Subtitles (SRT)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Generate standard SubRip SRT file with timestamped cues",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentProject != null && cues.isNotEmpty()) {
                        Button(
                            onClick = {
                                val srt = buildSrt(cues)
                                exportedSrtText = srt
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("VoxDub SRT", srt)
                                clipboard.setPrimaryClip(clip)
                                onShowMessage("SRT Subtitles copied to clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_srt_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export & Copy SRT to Clipboard")
                        }

                        if (exportedSrtText != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioSurface)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    exportedSrtText ?: "",
                                    fontSize = 11.sp,
                                    color = Color.LightGray,
                                    maxLines = 8
                                )
                            }
                        }
                    } else {
                        Text(
                            "Select an active project with cues in Library to export SRT subtitles.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // About & Architecture
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = StudioAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "About VoxDub AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "• Full-Screen Video Player with Edge-to-Edge display and HUD controls\n" +
                                "• External Link Importer: YouTube, Google Drive, and Direct MP4 Streams\n" +
                                "• Audio-to-Subtitles: Neural audio extraction with speaker gender/age auto-detection\n" +
                                "• Multi-speaker TTS Dubbing: Arthur, Kore, Sophia, David, Alex, Elena with human acoustic tuning\n" +
                                "• Studio Audio Mixing: Independent background/speech volume, dynamic ducking (-80%), and light spatial reverb\n" +
                                "• Multi-Language Support: Spanish, Hindi, French, German, Japanese, Portuguese, Chinese, Italian, Arabic",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Version 1.0 • Built with Jetpack Compose & Kotlin",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun buildSrt(cues: List<SubtitleCue>): String {
    val sb = StringBuilder()
    cues.forEachIndexed { index, cue ->
        sb.append("${index + 1}\n")
        sb.append("${formatSrtTime(cue.startTimeMs)} --> ${formatSrtTime(cue.endTimeMs)}\n")
        val text = if (cue.translatedText.isNotBlank()) cue.translatedText else cue.originalText
        sb.append("${cue.speaker}: $text\n\n")
    }
    return sb.toString().trim()
}

private fun formatSrtTime(millis: Long): String {
    val hours = (millis / 3600000).toInt()
    val minutes = ((millis % 3600000) / 60000).toInt()
    val seconds = ((millis % 60000) / 1000).toInt()
    val ms = (millis % 1000).toInt()
    return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, minutes, seconds, ms)
}
