package com.example.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.SampleDataProvider
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun PlayerScreen(
    project: VideoProject?,
    cues: List<SubtitleCue>,
    activeCue: SubtitleCue?,
    isDucked: Boolean,
    isDubbingEnabled: Boolean,
    isSubtitlesVisible: Boolean,
    isOriginalMuted: Boolean,
    isFullscreen: Boolean,
    seekRequest: Long?,
    onPlaybackPositionUpdate: (pos: Long, dur: Long) -> Unit,
    onTogglePlayPause: (Boolean) -> Unit,
    onToggleDubbing: () -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleOriginalMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onSeekRequest: (Long) -> Unit,
    onClearSeekRequest: () -> Unit,
    onUpdateAudioMix: (origVol: Float, dubVol: Float, duck: Boolean, reverb: Boolean) -> Unit,
    onNavigateToDubbing: () -> Unit
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
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No Video Selected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Select a project from the Library to play with dubbed audio and subtitles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(project.durationMs.coerceAtLeast(1000L)) }
    var showControls by remember { mutableStateOf(true) }

    // Audio Mix parameters
    var originalVol by remember(project.id) { mutableFloatStateOf(project.originalAudioVolume) }
    var dubbedVol by remember(project.id) { mutableFloatStateOf(project.dubbedAudioVolume) }
    var duckEnabled by remember(project.id) { mutableStateOf(project.audioDuckEnabled) }
    var reverbEnabled by remember(project.id) { mutableStateOf(project.reverbEnabled) }

    // Dynamic Volume Ducking application to MediaPlayer
    LaunchedEffect(isDucked, isOriginalMuted, originalVol, duckEnabled) {
        val effectiveVol = if (isOriginalMuted) 0f
        else if (duckEnabled && isDucked) (originalVol * 0.2f).coerceIn(0f, 1f)
        else originalVol.coerceIn(0f, 1f)

        try {
            mediaPlayerRef?.setVolume(effectiveVol, effectiveVol)
        } catch (e: Exception) {
            // Ignore if player not ready
        }
    }

    // Handle external seek requests
    LaunchedEffect(seekRequest) {
        if (seekRequest != null) {
            videoViewRef?.seekTo(seekRequest.toInt())
            currentPositionMs = seekRequest
            onClearSeekRequest()
        }
    }

    // Playback loop timer
    LaunchedEffect(isPlaying) {
        while (isActive && isPlaying) {
            val vv = videoViewRef
            if (vv != null) {
                val pos = vv.currentPosition.toLong()
                val dur = vv.duration.toLong().coerceAtLeast(1000L)
                currentPositionMs = pos
                if (dur > 0) durationMs = dur
                onPlaybackPositionUpdate(pos, dur)
            }
            delay(200)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Viewport & Overlays Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isFullscreen) Modifier.fillMaxSize()
                    else Modifier.aspectRatio(16f / 9f)
                )
                .background(Color.Black)
                .clickable { showControls = !showControls }
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.parse(project.videoUrl))
                        setOnPreparedListener { mp ->
                            mediaPlayerRef = mp
                            mp.isLooping = true
                            durationMs = mp.duration.toLong().coerceAtLeast(1000L)
                            val effectiveVol = if (isOriginalMuted) 0f
                            else if (duckEnabled && isDucked) (originalVol * 0.2f).coerceIn(0f, 1f)
                            else originalVol.coerceIn(0f, 1f)
                            mp.setVolume(effectiveVol, effectiveVol)
                            start()
                            isPlaying = true
                            onTogglePlayPause(true)
                        }
                        setOnCompletionListener {
                            isPlaying = false
                            onTogglePlayPause(false)
                        }
                        setOnErrorListener { _, _, _ ->
                            // Fallback gracefully on network stream error
                            true
                        }
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    videoViewRef = vv
                }
            )

            // Subtitle Banner Overlay (Bottom of Video)
            if (isSubtitlesVisible && activeCue != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = if (showControls) 54.dp else 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .border(1.dp, ElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "🗣 ${activeCue.speaker}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AcousticCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isDubbingEnabled) "• Dubbed (${project.targetLanguage.uppercase()})" else "• Original",
                                fontSize = 10.sp,
                                color = StudioAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isDubbingEnabled && activeCue.translatedText.isNotBlank()) activeCue.translatedText
                            else activeCue.originalText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // HUD Controls Overlay (Fade in/out on tap)
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    // Top HUD Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                project.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val targetLang = SampleDataProvider.getLanguageByCode(project.targetLanguage)
                            Text(
                                "Dubbed: ${targetLang.flag} ${targetLang.displayName} (${project.voiceName})",
                                fontSize = 11.sp,
                                color = AcousticCyan
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Subtitle Toggle
                            IconButton(onClick = onToggleSubtitles) {
                                Icon(
                                    if (isSubtitlesVisible) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
                                    contentDescription = "Toggle Subtitles",
                                    tint = if (isSubtitlesVisible) AcousticCyan else Color.Gray
                                )
                            }

                            // Fullscreen Toggle
                            IconButton(onClick = onToggleFullscreen) {
                                Icon(
                                    if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Center Play/Pause & Skip Buttons
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val target = (currentPositionMs - 5000L).coerceAtLeast(0L)
                                videoViewRef?.seekTo(target.toInt())
                                currentPositionMs = target
                                onSeekRequest(target)
                            }
                        ) {
                            Icon(
                                Icons.Default.Replay5,
                                contentDescription = "Rewind 5s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet.copy(alpha = 0.9f))
                                .clickable {
                                    val vv = videoViewRef ?: return@clickable
                                    if (isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                        onTogglePlayPause(false)
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                        onTogglePlayPause(true)
                                    }
                                }
                                .testTag("player_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val target = (currentPositionMs + 5000L).coerceAtMost(durationMs)
                                videoViewRef?.seekTo(target.toInt())
                                currentPositionMs = target
                                onSeekRequest(target)
                            }
                        ) {
                            Icon(
                                Icons.Default.Forward5,
                                contentDescription = "Forward 5s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Bottom Seekbar & Status HUD
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Speech Ducking Active Indicator
                        if (isDucked && isDubbingEnabled) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ElectricViolet.copy(alpha = 0.85f))
                                    .padding(horizontal = 10.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🎙 Dubbing Active • Speech Ducking -80%", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Seekbar
                        Slider(
                            value = currentPositionMs.toFloat(),
                            onValueChange = { newPos ->
                                currentPositionMs = newPos.toLong()
                            },
                            onValueChangeFinished = {
                                videoViewRef?.seekTo(currentPositionMs.toInt())
                                onSeekRequest(currentPositionMs)
                            },
                            valueRange = 0f..durationMs.toFloat().coerceAtLeast(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricViolet,
                                activeTrackColor = ElectricViolet,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("player_seekbar")
                        )

                        // Time stamps & Quick toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Dubbing Audio Toggle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isDubbingEnabled) ElectricViolet.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.15f))
                                        .clickable { onToggleDubbing() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("toggle_dubbing_button")
                                ) {
                                    Text(
                                        if (isDubbingEnabled) "Dubbing: ON" else "Dubbing: OFF",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDubbingEnabled) ElectricViolet else Color.LightGray
                                    )
                                }

                                // Original Audio Mute Toggle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!isOriginalMuted) AcousticCyan.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.15f))
                                        .clickable { onToggleOriginalMute() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        if (!isOriginalMuted) "Background: ON" else "Background: MUTED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isOriginalMuted) AcousticCyan else Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Below Video Section (When not in fullscreen)
        if (!isFullscreen) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(StudioSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Audio Mixing & Levels Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AcousticCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Studio Audio Mixing", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = onNavigateToDubbing,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Dubbing Studio", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Original Audio Volume Slider
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isOriginalMuted) Icons.Default.VolumeMute else Icons.Default.VolumeDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Background Video Audio: ${(originalVol * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Slider(
                                value = originalVol,
                                onValueChange = {
                                    originalVol = it
                                    onUpdateAudioMix(originalVol, dubbedVol, duckEnabled, reverbEnabled)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = AcousticCyan,
                                    activeTrackColor = AcousticCyan
                                )
                            )

                            // Dubbed Voice Volume Slider
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Dubbed Speech Track: ${(dubbedVol * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Slider(
                                value = dubbedVol,
                                onValueChange = {
                                    dubbedVol = it
                                    onUpdateAudioMix(originalVol, dubbedVol, duckEnabled, reverbEnabled)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricViolet,
                                    activeTrackColor = ElectricViolet
                                )
                            )

                            // Ducking & Reverb Toggles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Auto Speech Ducking", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = duckEnabled,
                                        onCheckedChange = {
                                            duckEnabled = it
                                            onUpdateAudioMix(originalVol, dubbedVol, duckEnabled, reverbEnabled)
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricViolet)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Light Reverb", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = reverbEnabled,
                                        onCheckedChange = {
                                            reverbEnabled = it
                                            onUpdateAudioMix(originalVol, dubbedVol, duckEnabled, reverbEnabled)
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AcousticCyan)
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Synchronized Cue Transcript
                item {
                    Text(
                        "Synchronized Cues Transcript (${cues.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(cues) { cue ->
                    val isCurrentCue = activeCue?.id == cue.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentCue) ElectricViolet.copy(alpha = 0.2f) else StudioSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isCurrentCue) 1.5.dp else 0.dp,
                                color = if (isCurrentCue) ElectricViolet else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                videoViewRef?.seekTo(cue.startTimeMs.toInt())
                                currentPositionMs = cue.startTimeMs
                                onSeekRequest(cue.startTimeMs)
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isCurrentCue) ElectricViolet else StudioBorder)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "${formatTime(cue.startTimeMs)} - ${formatTime(cue.endTimeMs)}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        cue.speaker,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AcousticCyan
                                    )
                                }

                                if (isCurrentCue) {
                                    Text("▶ NOW PLAYING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricViolet)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (cue.translatedText.isNotBlank()) cue.translatedText else cue.originalText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrentCue) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )

                            if (cue.translatedText.isNotBlank() && cue.originalText != cue.translatedText) {
                                Text(
                                    text = "Orig: ${cue.originalText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoViewRef?.stopPlayback()
            mediaPlayerRef?.release()
            mediaPlayerRef = null
            videoViewRef = null
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
