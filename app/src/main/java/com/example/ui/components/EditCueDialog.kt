package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubtitleCue
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioCoral
import com.example.ui.theme.StudioSurface

@Composable
fun EditCueDialog(
    cue: SubtitleCue,
    onSave: (SubtitleCue) -> Unit,
    onDelete: (cueId: Long) -> Unit,
    onTestDub: (SubtitleCue) -> Unit,
    onDismiss: () -> Unit
) {
    var speaker by remember { mutableStateOf(cue.speaker) }
    var gender by remember { mutableStateOf(cue.detectedGender) }
    var age by remember { mutableStateOf(cue.detectedAge) }
    var startTimeSec by remember { mutableStateOf((cue.startTimeMs / 1000f).toString()) }
    var endTimeSec by remember { mutableStateOf((cue.endTimeMs / 1000f).toString()) }
    var originalText by remember { mutableStateOf(cue.originalText) }
    var translatedText by remember { mutableStateOf(cue.translatedText) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = AcousticCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (cue.id == 0L) "Add Subtitle Cue" else "Edit Subtitle Cue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Time Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTimeSec,
                        onValueChange = { startTimeSec = it },
                        label = { Text("Start (sec)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cue_start_time")
                    )
                    OutlinedTextField(
                        value = endTimeSec,
                        onValueChange = { endTimeSec = it },
                        label = { Text("End (sec)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cue_end_time")
                    )
                }

                // Speaker Name
                OutlinedTextField(
                    value = speaker,
                    onValueChange = { speaker = it },
                    label = { Text("Speaker") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cue_speaker_name")
                )

                // Speaker Gender Chips
                Text("Speaker Voice Type:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Male", "Female").forEach { g ->
                        FilterChip(
                            selected = gender.equals(g, ignoreCase = true),
                            onClick = { gender = g },
                            label = { Text(g, fontSize = 12.sp) }
                        )
                    }
                }

                // Original Text
                OutlinedTextField(
                    value = originalText,
                    onValueChange = { originalText = it },
                    label = { Text("Original Audio Speech") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cue_original_text")
                )

                // Translated Dubbed Text
                OutlinedTextField(
                    value = translatedText,
                    onValueChange = { translatedText = it },
                    label = { Text("Dubbed Translated Text") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cue_translated_text")
                )

                // Test Dubbing Listen Button
                OutlinedButton(
                    onClick = {
                        val currentUpdated = cue.copy(
                            speaker = speaker,
                            detectedGender = gender,
                            detectedAge = age,
                            originalText = originalText,
                            translatedText = translatedText
                        )
                        onTestDub(currentUpdated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_dub_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElectricViolet)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Dub With TTS Voice")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startMs = (startTimeSec.toFloatOrNull() ?: 0f) * 1000L
                    val endMs = (endTimeSec.toFloatOrNull() ?: (startMs / 1000f + 4f)) * 1000L
                    val updated = cue.copy(
                        speaker = speaker,
                        detectedGender = gender,
                        detectedAge = age,
                        startTimeMs = startMs.toLong(),
                        endTimeMs = endMs.toLong(),
                        originalText = originalText,
                        translatedText = translatedText
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.testTag("save_cue_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (cue.id != 0L) {
                    IconButton(
                        onClick = { onDelete(cue.id) },
                        modifier = Modifier.testTag("delete_cue_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StudioCoral)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        containerColor = StudioSurface
    )
}
