package com.example.audio

import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioMixerController(
    private val ttsManager: TtsDubbingManager
) {
    private val _isDucked = MutableStateFlow(false)
    val isDucked: StateFlow<Boolean> = _isDucked.asStateFlow()

    private val _activeCue = MutableStateFlow<SubtitleCue?>(null)
    val activeCue: StateFlow<SubtitleCue?> = _activeCue.asStateFlow()

    private var lastSpokenCueId: Long = -1L
    private var isDubbingActive = true

    init {
        ttsManager.onSpeakingStarted = {
            _isDucked.value = true
        }
        ttsManager.onSpeakingFinished = {
            _isDucked.value = false
        }
    }

    fun setDubbingActive(active: Boolean) {
        isDubbingActive = active
        if (!active) {
            ttsManager.stop()
            _isDucked.value = false
        }
    }

    /**
     * Calculates the effective volume for the original video audio player.
     * Takes into account the configured original audio volume, muting, and speech ducking.
     */
    fun calculateEffectiveOriginalVolume(
        baseVolume: Float,
        duckEnabled: Boolean,
        isMuted: Boolean
    ): Float {
        if (isMuted) return 0f
        return if (duckEnabled && _isDucked.value) {
            (baseVolume * 0.2f).coerceIn(0f, 1f)
        } else {
            baseVolume.coerceIn(0f, 1f)
        }
    }

    /**
     * Called continuously during video playback with current position in milliseconds.
     * Checks if a subtitle cue should be spoken or displayed.
     */
    fun onPlaybackPositionUpdate(
        currentPositionMs: Long,
        cues: List<SubtitleCue>,
        project: VideoProject?
    ) {
        val currentCue = cues.firstOrNull { cue ->
            currentPositionMs >= cue.startTimeMs && currentPositionMs <= cue.endTimeMs
        }

        _activeCue.value = currentCue

        if (currentCue != null) {
            if (isDubbingActive && currentCue.id != lastSpokenCueId && project != null) {
                lastSpokenCueId = currentCue.id
                val textToSpeak = if (project.isDubbed && currentCue.translatedText.isNotBlank()) {
                    currentCue.translatedText
                } else {
                    currentCue.originalText
                }

                ttsManager.speakCue(
                    text = textToSpeak,
                    langCode = if (project.isDubbed) project.targetLanguage else project.sourceLanguage,
                    pitch = project.voicePitch,
                    speed = project.voiceSpeed,
                    voiceGender = currentCue.detectedGender
                )
            }
        } else {
            if (lastSpokenCueId != -1L) {
                // We moved out of the previous cue
                lastSpokenCueId = -1L
            }
        }
    }

    fun onSeek() {
        ttsManager.stop()
        lastSpokenCueId = -1L
        _isDucked.value = false
    }

    fun onPause() {
        ttsManager.stop()
        _isDucked.value = false
    }
}
