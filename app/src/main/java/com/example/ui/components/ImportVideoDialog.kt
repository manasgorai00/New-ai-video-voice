package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.SampleDataProvider
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportVideoDialog(
    onDismiss: () -> Unit,
    onImport: (title: String, url: String, sourceType: String, targetLang: String, voiceId: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Link/URL, 1: YouTube, 2: Google Drive, 3: Sample
    var title by remember { mutableStateOf("") }
    var urlInput by remember { mutableStateOf("") }
    var selectedLangCode by remember { mutableStateOf("es") }
    var selectedVoiceId by remember { mutableStateOf("arthur") }

    var langDropdownExpanded by remember { mutableStateOf(false) }
    var voiceDropdownExpanded by remember { mutableStateOf(false) }

    val tabs = listOf("Direct Link", "YouTube", "Drive", "Samples")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = ElectricViolet,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Import Video for Dubbing",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = StudioSurfaceVariant,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                if (index == 1 && urlInput.isBlank()) {
                                    urlInput = "https://www.youtube.com/watch?v=sample_video"
                                    if (title.isBlank()) title = "YouTube Video Dub"
                                } else if (index == 2 && urlInput.isBlank()) {
                                    urlInput = "https://drive.google.com/file/d/sample_file/view"
                                    if (title.isBlank()) title = "Google Drive Clip"
                                }
                            },
                            text = { Text(tabTitle, fontSize = 12.sp, maxLines = 1) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 3) {
                    // Samples selector
                    Text(
                        "Choose a pre-configured video clip:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SampleDataProvider.sampleProjects) { sample ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        title = sample.title
                                        urlInput = sample.videoUrl
                                        selectedLangCode = sample.targetLanguage
                                        selectedVoiceId = sample.voiceId
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = AcousticCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            sample.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            "Target: ${sample.targetLanguage.uppercase()} • Voice: ${sample.voiceName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Form fields
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Video Title") },
                        placeholder = { Text("e.g. AI Keynote Presentation") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_video_title")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val urlLabel = when (selectedTab) {
                        1 -> "YouTube Link"
                        2 -> "Google Drive Share Link"
                        else -> "External Video URL (MP4 / WebM / HLS)"
                    }
                    val urlPlaceholder = when (selectedTab) {
                        1 -> "https://youtube.com/watch?v=..."
                        2 -> "https://drive.google.com/file/d/.../view"
                        else -> "https://example.com/video.mp4"
                    }

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text(urlLabel) },
                        placeholder = { Text(urlPlaceholder) },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_video_url")
                    )

                    if (selectedTab == 1) {
                        Text(
                            "YouTube links are parsed for audio extraction, speech transcription, and multilingual TTS sync.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    } else if (selectedTab == 2) {
                        Text(
                            "Google Drive links must have 'Anyone with link can view' permission for stream extraction.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Target Language & Voice Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { langDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val currLang = SampleDataProvider.getLanguageByCode(selectedLangCode)
                            Text("${currLang.flag} ${currLang.displayName}", fontSize = 13.sp)
                        }
                        DropdownMenu(
                            expanded = langDropdownExpanded,
                            onDismissRequest = { langDropdownExpanded = false }
                        ) {
                            SampleDataProvider.supportedLanguages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.displayName} (${lang.nativeName})") },
                                    onClick = {
                                        selectedLangCode = lang.code
                                        langDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Voice Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { voiceDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val currVoice = SampleDataProvider.getVoiceById(selectedVoiceId)
                            Text("🗣 ${currVoice.name}", fontSize = 13.sp)
                        }
                        DropdownMenu(
                            expanded = voiceDropdownExpanded,
                            onDismissRequest = { voiceDropdownExpanded = false }
                        ) {
                            SampleDataProvider.availableVoices.forEach { voice ->
                                DropdownMenuItem(
                                    text = { Text("${voice.name} (${voice.gender} • ${voice.toneProfile})") },
                                    onClick = {
                                        selectedVoiceId = voice.id
                                        voiceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTitle = title.ifBlank {
                        when (selectedTab) {
                            1 -> "YouTube Video Dub"
                            2 -> "Google Drive Video"
                            else -> "External Stream Project"
                        }
                    }
                    val finalUrl = urlInput.ifBlank {
                        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    }
                    val sourceType = when (selectedTab) {
                        1 -> "YOUTUBE"
                        2 -> "GDRIVE"
                        3 -> "SAMPLE"
                        else -> "LINK"
                    }
                    onImport(finalTitle, finalUrl, sourceType, selectedLangCode, selectedVoiceId)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("submit_import_button")
            ) {
                Text("Start Dubbing")
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
