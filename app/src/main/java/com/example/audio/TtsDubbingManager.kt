package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.data.SampleDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsDubbingManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _engineStatus = MutableStateFlow("Initializing...")
    val engineStatus: StateFlow<String> = _engineStatus.asStateFlow()

    private val _availableVoiceCount = MutableStateFlow(0)
    val availableVoiceCount: StateFlow<Int> = _availableVoiceCount.asStateFlow()

    var onSpeakingStarted: (() -> Unit)? = null
    var onSpeakingFinished: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            _engineStatus.value = "TTS Engine Ready (${tts?.defaultEngine ?: "Default"})"

            tts?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )

            try {
                val voices = tts?.voices
                if (voices != null) {
                    _availableVoiceCount.value = voices.size
                }
            } catch (e: Exception) {
                _availableVoiceCount.value = 6
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    onSpeakingStarted?.invoke()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeakingFinished?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeakingFinished?.invoke()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    onSpeakingFinished?.invoke()
                }
            })
        } else {
            _engineStatus.value = "TTS Initialization Failed"
        }
    }

    /**
     * Speaks the translated subtitle cue with specified pitch and rate.
     */
    fun speakCue(
        text: String,
        langCode: String,
        pitch: Float,
        speed: Float,
        voiceGender: String = "Male"
    ) {
        if (!isInitialized || tts == null || text.isBlank()) return

        val lang = SampleDataProvider.getLanguageByCode(langCode)
        tts?.language = lang.locale

        // Adjust pitch: Male baritone slightly lower (0.9), Female higher (1.15)
        val adjustedPitch = when (voiceGender.lowercase()) {
            "female" -> (pitch * 1.15f).coerceIn(0.5f, 2.0f)
            else -> (pitch * 0.95f).coerceIn(0.5f, 2.0f)
        }

        tts?.setPitch(adjustedPitch)
        tts?.setSpeechRate(speed.coerceIn(0.5f, 2.0f))

        // Select matching system voice if available
        try {
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val matchingVoice = voices.firstOrNull { voice ->
                    voice.locale.language == lang.locale.language &&
                            (if (voiceGender.lowercase() == "female") voice.name.contains("female", ignoreCase = true)
                            else voice.name.contains("male", ignoreCase = true))
                }
                if (matchingVoice != null) {
                    tts?.voice = matchingVoice
                }
            }
        } catch (e: Exception) {
            // Fallback to default voice
        }

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "cue_${System.currentTimeMillis()}")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "cue_${System.currentTimeMillis()}")
    }

    /**
     * Previews a voice by speaking its sample phrase or custom text.
     */
    fun previewVoice(voiceId: String, pitch: Float, speed: Float) {
        val voice = SampleDataProvider.getVoiceById(voiceId)
        speakCue(
            text = voice.samplePhrase,
            langCode = "en",
            pitch = pitch,
            speed = speed,
            voiceGender = voice.gender
        )
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
