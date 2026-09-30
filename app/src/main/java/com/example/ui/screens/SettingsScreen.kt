package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApiKeyManager
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCoral
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.StudioSurfaceVariant
import java.util.Locale

@Composable
fun SettingsScreen(
    currentProject: VideoProject?,
    cues: List<SubtitleCue>,
    engineStatus: String,
    availableVoiceCount: Int,
    apiKeyManager: ApiKeyManager,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputApiKey by remember { mutableStateOf(apiKeyManager.getSavedCustomKey()) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var exportedSrtText by remember { mutableStateOf<String?>(null) }
    var isGithubWorkflowExpanded by remember { mutableStateOf(true) }

    val effectiveApiKey = apiKeyManager.getEffectiveApiKey()
    val isKeyConfigured = effectiveApiKey.isNotBlank()
    val keySource = apiKeyManager.getKeySource()
    val maskedKey = apiKeyManager.getMaskedKey()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Settings & Configuration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // ==================== SECTION 1: SET API KEY ====================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("api_key_settings_card")
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
                                Icon(
                                    Icons.Default.Key,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Gemini & Translation API Key",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Required for cloud AI dubbing & translation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isKeyConfigured) ElectricViolet.copy(alpha = 0.2f) else StudioCoral.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                if (isKeyConfigured) "ACTIVE" else "NOT SET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isKeyConfigured) ElectricViolet else StudioCoral
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Key Status Info Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioSurface)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Active Source:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    keySource,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcousticCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Key Value:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    maskedKey,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input TextField for custom key
                    OutlinedTextField(
                        value = inputApiKey,
                        onValueChange = { inputApiKey = it },
                        label = { Text("Set Custom API Key") },
                        placeholder = { Text("Enter or paste your Gemini API key") },
                        singleLine = true,
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            onSaveApiKey(inputApiKey)
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_api_key_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Save & Reset buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                onSaveApiKey(inputApiKey)
                                onShowMessage("API Key saved successfully!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("save_api_key_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Key", fontSize = 12.sp)
                        }

                        if (inputApiKey.isNotBlank() || isKeyConfigured) {
                            OutlinedButton(
                                onClick = {
                                    inputApiKey = ""
                                    onClearApiKey()
                                    onShowMessage("Custom API key cleared")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clear_api_key_button")
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Clear Key", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Tip: You can also configure your Gemini API Key in the AI Studio Secrets panel. The app checks your custom key first, then falls back to BuildConfig automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // ==================== SECTION 2: HOW TO BUILD APK VIA GITHUB WORKFLOW ====================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("github_workflow_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isGithubWorkflowExpanded = !isGithubWorkflowExpanded },
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
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = AcousticCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Build APK with GitHub Workflow",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Step-by-step automated CI/CD compilation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AcousticCyan
                                )
                            }
                        }

                        IconButton(onClick = { isGithubWorkflowExpanded = !isGithubWorkflowExpanded }) {
                            Icon(
                                if (isGithubWorkflowExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = Color.White
                            )
                        }
                    }

                    if (isGithubWorkflowExpanded) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Step by step cards
                        val steps = listOf(
                            Pair("1. Push Code to GitHub", "Commit all files including `.github/workflows/build-apk.yml` to your GitHub repository `main` branch."),
                            Pair("2. (Optional) Set GEMINI_API_KEY in GitHub Secrets", "Go to your GitHub repo > Settings > Secrets and variables > Actions > New repository secret with name `GEMINI_API_KEY`."),
                            Pair("3. Trigger GitHub Actions Workflow", "The workflow runs automatically on every push, or manually: Go to GitHub 'Actions' tab > 'Build Android APK' > 'Run workflow'."),
                            Pair("4. Download Built APK Artifact", "When the build finishes with a green checkmark, scroll down to the 'Artifacts' section and download 'VoxDub-AI-Video-Dubber-Debug-APK'."),
                            Pair("5. Install on Android Device", "Extract the downloaded ZIP to get `app-debug.apk`. Transfer to your phone and install!")
                        )

                        steps.forEachIndexed { index, (stepTitle, stepDesc) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StudioSurface)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        stepTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StudioAmber
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        stepDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Copy Workflow File button
                        Button(
                            onClick = {
                                val workflowContent = getWorkflowYamlText()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("GitHub Workflow YAML", workflowContent)
                                clipboard.setPrimaryClip(clip)
                                onShowMessage("Workflow YAML copied to clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AcousticCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copy_workflow_yaml_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF00363D), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy GitHub Workflow YAML", color = Color(0xFF00363D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ==================== SECTION 3: TTS SYNTHESIZER STATUS ====================
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

        // ==================== SECTION 4: SUBTITLE EXPORT ====================
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

        // ==================== SECTION 5: ABOUT ====================
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
                                "• Multi-Language Support: Spanish, Hindi, French, German, Japanese, Portuguese, Chinese, Italian, Arabic\n" +
                                "• Automated GitHub Actions CI/CD workflow ready to compile APK",
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

private fun getWorkflowYamlText(): String {
    return """
name: Build Android APK

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    name: Build & Package APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Prepare Secrets and Keystore
        run: |
          if [ ! -f .env ]; then
            cp .env.example .env
          fi
          if [ -n "${'$'}{{ secrets.GEMINI_API_KEY }}" ]; then
            echo "GEMINI_API_KEY=${'$'}{{ secrets.GEMINI_API_KEY }}" > .env
          fi
          if [ ! -f debug.keystore ] && [ -f debug.keystore.base64 ]; then
            base64 -d debug.keystore.base64 > debug.keystore
          fi
          chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload Debug APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: VoxDub-AI-Video-Dubber-Debug-APK
          path: app/build/outputs/apk/debug/*.apk
          if-no-files-found: error
          retention-days: 14
    """.trimIndent()
}
