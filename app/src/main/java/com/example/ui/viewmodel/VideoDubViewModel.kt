package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioMixerController
import com.example.audio.TtsDubbingManager
import com.example.data.AppDatabase
import com.example.data.SampleDataProvider
import com.example.data.VideoDubRepository
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    LIBRARY,
    PLAYER,
    DUBBING,
    SETTINGS
}

class VideoDubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoDubRepository
    val ttsManager: TtsDubbingManager
    val mixerController: AudioMixerController

    val allProjects: StateFlow<List<VideoProject>>

    private val _currentTab = MutableStateFlow(AppTab.LIBRARY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _currentCues = MutableStateFlow<List<SubtitleCue>>(emptyList())
    val currentCues: StateFlow<List<SubtitleCue>> = _currentCues.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackPositionMs = MutableStateFlow(0L)
    val playbackPositionMs: StateFlow<Long> = _playbackPositionMs.asStateFlow()

    private val _videoDurationMs = MutableStateFlow(0L)
    val videoDurationMs: StateFlow<Long> = _videoDurationMs.asStateFlow()

    private val _isDubbingEnabled = MutableStateFlow(true)
    val isDubbingEnabled: StateFlow<Boolean> = _isDubbingEnabled.asStateFlow()

    private val _isSubtitlesVisible = MutableStateFlow(true)
    val isSubtitlesVisible: StateFlow<Boolean> = _isSubtitlesVisible.asStateFlow()

    private val _isOriginalMuted = MutableStateFlow(false)
    val isOriginalMuted: StateFlow<Boolean> = _isOriginalMuted.asStateFlow()

    private val _isFullscreen = MutableStateFlow(false)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen.asStateFlow()

    private val _isProcessingAudioToSubtitles = MutableStateFlow(false)
    val isProcessingAudioToSubtitles: StateFlow<Boolean> = _isProcessingAudioToSubtitles.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private var cuesObservationJob: Job? = null
    private var seekRequest = MutableStateFlow<Long?>(null)
    val seekRequestFlow: StateFlow<Long?> = seekRequest.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).videoDubDao()
        repository = VideoDubRepository(dao)
        ttsManager = TtsDubbingManager(application)
        mixerController = AudioMixerController(ttsManager)

        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeSampleDataIfEmpty()
            // Default select the first project
            allProjects.collect { projects ->
                if (_currentProject.value == null && projects.isNotEmpty()) {
                    selectProject(projects.first(), switchTab = false)
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectProject(project: VideoProject, switchTab: Boolean = true) {
        _currentProject.value = project
        _isDubbingEnabled.value = project.isDubbed
        mixerController.setDubbingActive(project.isDubbed)

        // Observe cues for this project
        cuesObservationJob?.cancel()
        cuesObservationJob = viewModelScope.launch {
            repository.getCuesForProject(project.id).collect { cues ->
                _currentCues.value = cues
            }
        }

        if (switchTab) {
            _currentTab.value = AppTab.PLAYER
        }
    }

    fun importVideo(
        title: String,
        url: String,
        sourceType: String,
        targetLang: String = "es",
        voiceId: String = "arthur"
    ) {
        viewModelScope.launch {
            _isProcessingAudioToSubtitles.value = true
            try {
                val newId = repository.createProject(
                    title = title.ifBlank { "Untitled Dubbing Video" },
                    videoUrl = url.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
                    sourceType = sourceType,
                    targetLanguage = targetLang,
                    voiceId = voiceId
                )
                val newProject = repository.getProjectDirect(newId)
                if (newProject != null) {
                    selectProject(newProject, switchTab = true)
                    _userMessage.value = "Imported & auto-generated subtitles successfully!"
                }
            } catch (e: Exception) {
                _userMessage.value = "Error importing video: ${e.localizedMessage}"
            } finally {
                _isProcessingAudioToSubtitles.value = false
            }
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_currentProject.value?.id == projectId) {
                _currentProject.value = null
                _currentCues.value = emptyList()
            }
            _userMessage.value = "Project deleted"
        }
    }

    fun updateVoiceMode(voiceId: String, pitch: Float, speed: Float, tone: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            repository.updateVoiceMode(project.id, voiceId, pitch, speed, tone)
            _currentProject.value = repository.getProjectDirect(project.id)
            _userMessage.value = "Voice mode updated: ${SampleDataProvider.getVoiceById(voiceId).name}"
        }
    }

    fun updateAudioMix(
        origVol: Float,
        dubVol: Float,
        duckEnabled: Boolean,
        reverbEnabled: Boolean
    ) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            repository.updateAudioMix(project.id, origVol, dubVol, duckEnabled, reverbEnabled)
            _currentProject.value = repository.getProjectDirect(project.id)
        }
    }

    fun updateTargetLanguage(langCode: String) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _isProcessingAudioToSubtitles.value = true
            repository.updateTargetLanguage(project.id, langCode)
            _currentProject.value = repository.getProjectDirect(project.id)
            _isProcessingAudioToSubtitles.value = false
            val lang = SampleDataProvider.getLanguageByCode(langCode)
            _userMessage.value = "Subtitles & dubbing translated into ${lang.displayName}!"
        }
    }

    fun runAudioToSubtitles() {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            _isProcessingAudioToSubtitles.value = true
            repository.runAudioToSubtitlesExtraction(project.id)
            _currentProject.value = repository.getProjectDirect(project.id)
            _isProcessingAudioToSubtitles.value = false
            _userMessage.value = "Audio extracted! Timestamped cues & speakers generated."
        }
    }

    fun saveCue(cue: SubtitleCue) {
        viewModelScope.launch {
            repository.saveCue(cue)
            _userMessage.value = "Subtitle cue saved"
        }
    }

    fun deleteCue(cueId: Long) {
        val project = _currentProject.value ?: return
        viewModelScope.launch {
            repository.deleteCue(cueId, project.id)
            _userMessage.value = "Cue removed"
        }
    }

    fun previewVoice(voiceId: String, pitch: Float, speed: Float) {
        ttsManager.previewVoice(voiceId, pitch, speed)
    }

    fun testDubCue(cue: SubtitleCue) {
        val project = _currentProject.value ?: return
        ttsManager.speakCue(
            text = cue.translatedText.ifBlank { cue.originalText },
            langCode = project.targetLanguage,
            pitch = project.voicePitch,
            speed = project.voiceSpeed,
            voiceGender = cue.detectedGender
        )
    }

    fun onPlaybackPositionUpdate(positionMs: Long, durationMs: Long) {
        _playbackPositionMs.value = positionMs
        if (durationMs > 0 && _videoDurationMs.value != durationMs) {
            _videoDurationMs.value = durationMs
        }
        mixerController.onPlaybackPositionUpdate(positionMs, _currentCues.value, _currentProject.value)
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
        if (!playing) {
            mixerController.onPause()
        }
    }

    fun requestSeek(positionMs: Long) {
        seekRequest.value = positionMs
        _playbackPositionMs.value = positionMs
        mixerController.onSeek()
    }

    fun clearSeekRequest() {
        seekRequest.value = null
    }

    fun toggleDubbing() {
        val newState = !_isDubbingEnabled.value
        _isDubbingEnabled.value = newState
        mixerController.setDubbingActive(newState)
        _userMessage.value = if (newState) "Dubbing Voice Enabled" else "Dubbing Muted (Original Only)"
    }

    fun toggleSubtitles() {
        _isSubtitlesVisible.value = !_isSubtitlesVisible.value
    }

    fun toggleOriginalAudioMute() {
        _isOriginalMuted.value = !_isOriginalMuted.value
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
