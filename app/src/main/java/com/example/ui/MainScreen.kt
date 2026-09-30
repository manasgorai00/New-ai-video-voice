package com.example.ui

import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SubtitleCue
import com.example.ui.components.EditCueDialog
import com.example.ui.components.ImportVideoDialog
import com.example.ui.components.VoiceSelectionDialog
import com.example.ui.screens.DubbingStudioScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AcousticCyan
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.StudioSurface
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.VideoDubViewModel

@Composable
fun MainScreen(viewModel: VideoDubViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val currentProject by viewModel.currentProject.collectAsStateWithLifecycle()
    val currentCues by viewModel.currentCues.collectAsStateWithLifecycle()
    val activeCue by viewModel.mixerController.activeCue.collectAsStateWithLifecycle()
    val isDucked by viewModel.mixerController.isDucked.collectAsStateWithLifecycle()
    val isDubbingEnabled by viewModel.isDubbingEnabled.collectAsStateWithLifecycle()
    val isSubtitlesVisible by viewModel.isSubtitlesVisible.collectAsStateWithLifecycle()
    val isOriginalMuted by viewModel.isOriginalMuted.collectAsStateWithLifecycle()
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val seekRequest by viewModel.seekRequestFlow.collectAsStateWithLifecycle()
    val isProcessingAudio by viewModel.isProcessingAudioToSubtitles.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val engineStatus by viewModel.ttsManager.engineStatus.collectAsStateWithLifecycle()
    val voiceCount by viewModel.ttsManager.availableVoiceCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // Dialog states
    var showImportDialog by remember { mutableStateOf(false) }
    var showVoiceModeDialog by remember { mutableStateOf(false) }
    var cueToEdit by remember { mutableStateOf<SubtitleCue?>(null) }

    // Back handling: pop back to Library if on sub-screens
    BackHandler(enabled = currentTab != AppTab.LIBRARY) {
        if (isFullscreen) {
            viewModel.toggleFullscreen()
        } else {
            viewModel.setTab(AppTab.LIBRARY)
        }
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        bottomBar = {
            if (!isFullscreen) {
                NavigationBar(
                    containerColor = StudioSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar"),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.LIBRARY,
                        onClick = { viewModel.setTab(AppTab.LIBRARY) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.LIBRARY) Icons.Filled.Movie else Icons.Outlined.Movie,
                                contentDescription = "Library"
                            )
                        },
                        label = { Text("Library", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = ElectricViolet,
                            selectedTextColor = ElectricViolet,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_library")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.PLAYER,
                        onClick = { viewModel.setTab(AppTab.PLAYER) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.PLAYER) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                                contentDescription = "Player"
                            )
                        },
                        label = { Text("Player", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = ElectricViolet,
                            selectedTextColor = ElectricViolet,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_player")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.DUBBING,
                        onClick = { viewModel.setTab(AppTab.DUBBING) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.DUBBING) Icons.Filled.RecordVoiceOver else Icons.Outlined.RecordVoiceOver,
                                contentDescription = "Dubbing"
                            )
                        },
                        label = { Text("Dubbing", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = ElectricViolet,
                            selectedTextColor = ElectricViolet,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_dubbing")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.SETTINGS,
                        onClick = { viewModel.setTab(AppTab.SETTINGS) },
                        icon = {
                            Icon(
                                if (currentTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = ElectricViolet,
                            selectedTextColor = ElectricViolet,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            when (currentTab) {
                AppTab.LIBRARY -> {
                    LibraryScreen(
                        projects = allProjects,
                        currentProject = currentProject,
                        onSelectProject = { proj, toPlayer ->
                            viewModel.selectProject(proj, switchTab = toPlayer)
                            if (!toPlayer) viewModel.setTab(AppTab.DUBBING)
                        },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onOpenImportDialog = { showImportDialog = true }
                    )
                }

                AppTab.PLAYER -> {
                    PlayerScreen(
                        project = currentProject,
                        cues = currentCues,
                        activeCue = activeCue,
                        isDucked = isDucked,
                        isDubbingEnabled = isDubbingEnabled,
                        isSubtitlesVisible = isSubtitlesVisible,
                        isOriginalMuted = isOriginalMuted,
                        isFullscreen = isFullscreen,
                        seekRequest = seekRequest,
                        onPlaybackPositionUpdate = { pos, dur ->
                            viewModel.onPlaybackPositionUpdate(pos, dur)
                        },
                        onTogglePlayPause = { isPlaying ->
                            viewModel.setPlaying(isPlaying)
                        },
                        onToggleDubbing = { viewModel.toggleDubbing() },
                        onToggleSubtitles = { viewModel.toggleSubtitles() },
                        onToggleOriginalMute = { viewModel.toggleOriginalAudioMute() },
                        onToggleFullscreen = { viewModel.toggleFullscreen() },
                        onSeekRequest = { viewModel.requestSeek(it) },
                        onClearSeekRequest = { viewModel.clearSeekRequest() },
                        onUpdateAudioMix = { orig, dub, duck, rev ->
                            viewModel.updateAudioMix(orig, dub, duck, rev)
                        },
                        onNavigateToDubbing = { viewModel.setTab(AppTab.DUBBING) }
                    )
                }

                AppTab.DUBBING -> {
                    DubbingStudioScreen(
                        project = currentProject,
                        cues = currentCues,
                        isProcessingAudioToSubtitles = isProcessingAudio,
                        onRunAudioToSubtitles = { viewModel.runAudioToSubtitles() },
                        onOpenVoiceModeDialog = { showVoiceModeDialog = true },
                        onOpenEditCueDialog = { cue -> cueToEdit = cue },
                        onAddNewCue = {
                            val nextStartTime = (currentCues.maxOfOrNull { it.endTimeMs } ?: 0L) + 500L
                            cueToEdit = SubtitleCue(
                                id = 0L,
                                projectId = currentProject?.id ?: 0L,
                                startTimeMs = nextStartTime,
                                endTimeMs = nextStartTime + 4000L,
                                speaker = "Speaker 1",
                                detectedGender = "Male",
                                detectedAge = "Adult",
                                originalText = "",
                                translatedText = "",
                                assignedVoice = "Arthur"
                            )
                        },
                        onDeleteCue = { viewModel.deleteCue(it) },
                        onTestDubCue = { viewModel.testDubCue(it) },
                        onPreviewVoice = { voiceId, pitch, speed ->
                            viewModel.previewVoice(voiceId, pitch, speed)
                        },
                        onUpdateTargetLanguage = { viewModel.updateTargetLanguage(it) },
                        onUpdateAudioMix = { orig, dub, duck, rev ->
                            viewModel.updateAudioMix(orig, dub, duck, rev)
                        }
                    )
                }

                AppTab.SETTINGS -> {
                    SettingsScreen(
                        currentProject = currentProject,
                        cues = currentCues,
                        engineStatus = engineStatus,
                        availableVoiceCount = voiceCount,
                        apiKeyManager = viewModel.apiKeyManager,
                        onSaveApiKey = { key -> viewModel.saveApiKey(key) },
                        onClearApiKey = { viewModel.clearApiKey() },
                        onShowMessage = { msg ->
                            kotlinx.coroutines.CoroutineScope(scope.coroutineContext).let {
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        ImportVideoDialog(
            onDismiss = { showImportDialog = false },
            onImport = { title, url, sourceType, lang, voice ->
                viewModel.importVideo(title, url, sourceType, lang, voice)
                showImportDialog = false
            }
        )
    }

    // Set Voice Mode Dialog
    if (showVoiceModeDialog && currentProject != null) {
        val proj = currentProject!!
        VoiceSelectionDialog(
            initialVoiceId = proj.voiceId,
            initialPitch = proj.voicePitch,
            initialSpeed = proj.voiceSpeed,
            initialTone = proj.voiceTone,
            detectedGender = currentCues.firstOrNull()?.detectedGender,
            detectedAge = currentCues.firstOrNull()?.detectedAge,
            onPreviewVoice = { voiceId, pitch, speed ->
                viewModel.previewVoice(voiceId, pitch, speed)
            },
            onApply = { voiceId, pitch, speed, tone ->
                viewModel.updateVoiceMode(voiceId, pitch, speed, tone)
                showVoiceModeDialog = false
            },
            onDismiss = { showVoiceModeDialog = false }
        )
    }

    // Edit Cue Dialog
    if (cueToEdit != null) {
        EditCueDialog(
            cue = cueToEdit!!,
            onSave = { savedCue ->
                viewModel.saveCue(savedCue)
                cueToEdit = null
            },
            onDelete = { cueId ->
                viewModel.deleteCue(cueId)
                cueToEdit = null
            },
            onTestDub = { cue ->
                viewModel.testDubCue(cue)
            },
            onDismiss = { cueToEdit = null }
        )
    }
}
