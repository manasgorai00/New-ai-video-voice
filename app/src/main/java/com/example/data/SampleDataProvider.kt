package com.example.data

import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import java.util.Locale

data class DubVoice(
    val id: String,
    val name: String,
    val gender: String, // "Male" or "Female"
    val ageGroup: String, // "Adult", "Young Adult", "Senior"
    val description: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val toneProfile: String,
    val samplePhrase: String
)

data class SupportedLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String,
    val locale: Locale,
    val accentRegion: String
)

object SampleDataProvider {

    val supportedLanguages = listOf(
        SupportedLanguage("es", "Spanish", "Español", "🇪🇸", Locale("es", "ES"), "Castilian / LatAm"),
        SupportedLanguage("en", "English", "English", "🇺🇸", Locale.US, "General American"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳", Locale("hi", "IN"), "Standard Hindi"),
        SupportedLanguage("fr", "French", "Français", "🇫🇷", Locale.FRANCE, "Metropolitan French"),
        SupportedLanguage("de", "German", "Deutsch", "🇩🇪", Locale.GERMANY, "Standard German"),
        SupportedLanguage("ja", "Japanese", "日本語", "🇯🇵", Locale.JAPAN, "Standard Japanese"),
        SupportedLanguage("pt", "Portuguese", "Português", "🇧🇷", Locale("pt", "BR"), "Brazilian"),
        SupportedLanguage("it", "Italian", "Italiano", "🇮🇹", Locale.ITALY, "Standard Italian"),
        SupportedLanguage("zh", "Chinese", "中文", "🇨🇳", Locale.SIMPLIFIED_CHINESE, "Mandarin"),
        SupportedLanguage("ar", "Arabic", "العربية", "🇸🇦", Locale("ar", "SA"), "Modern Standard")
    )

    val availableVoices = listOf(
        DubVoice(
            id = "arthur",
            name = "Arthur",
            gender = "Male",
            ageGroup = "Adult",
            description = "Warm baritone voice, human breathing cadence, ideal for tech & documentaries",
            defaultPitch = 0.95f,
            defaultSpeed = 1.0f,
            toneProfile = "Natural Warm",
            samplePhrase = "Welcome to the next generation of artificial intelligence and speech synthesis."
        ),
        DubVoice(
            id = "kore",
            name = "Kore",
            gender = "Female",
            ageGroup = "Young Adult",
            description = "Crisp, dynamic, bright female voice with expressive inflection",
            defaultPitch = 1.15f,
            defaultSpeed = 1.05f,
            toneProfile = "Conversational",
            samplePhrase = "Dubbing videos into any language has never been this seamless and fast."
        ),
        DubVoice(
            id = "sophia",
            name = "Sophia",
            gender = "Female",
            ageGroup = "Adult",
            description = "Polished studio narrator, calm, clear acoustic fidelity",
            defaultPitch = 1.0f,
            defaultSpeed = 0.98f,
            toneProfile = "Studio Broadcast",
            samplePhrase = "In the heart of the ocean, mysteries unfold beneath the surface of time."
        ),
        DubVoice(
            id = "david",
            name = "David",
            gender = "Male",
            ageGroup = "Adult",
            description = "Authoritative, deep resonant male voice for cinematic trailers & news",
            defaultPitch = 0.85f,
            defaultSpeed = 0.95f,
            toneProfile = "Deep Narrative",
            samplePhrase = "Every innovation begins with a singular question: what if we could reach everyone?"
        ),
        DubVoice(
            id = "alex",
            name = "Alex",
            gender = "Male",
            ageGroup = "Young Adult",
            description = "Energetic, youthful casual tone for vlogs, gaming, and lifestyle reels",
            defaultPitch = 1.05f,
            defaultSpeed = 1.1f,
            toneProfile = "Conversational",
            samplePhrase = "Hey everyone! Check out how natural this dubbed voice sounds right here on Android."
        ),
        DubVoice(
            id = "elena",
            name = "Elena",
            gender = "Female",
            ageGroup = "Young Adult",
            description = "Melodic, empathetic and warm tone for storytelling and education",
            defaultPitch = 1.08f,
            defaultSpeed = 1.0f,
            toneProfile = "Natural Warm",
            samplePhrase = "Language connects us, bridging cultures and bringing our favorite stories to life."
        )
    )

    fun getVoiceById(id: String): DubVoice {
        return availableVoices.firstOrNull { it.id == id } ?: availableVoices.first()
    }

    fun getLanguageByCode(code: String): SupportedLanguage {
        return supportedLanguages.firstOrNull { it.code == code } ?: supportedLanguages.first()
    }

    // Curated initial projects with reliable public video streams
    val sampleProjects = listOf(
        VideoProject(
            id = 1L,
            title = "Quantum AI Keynote: The Future of Intelligence",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            sourceType = "SAMPLE",
            durationMs = 60000L,
            sourceLanguage = "en",
            targetLanguage = "es",
            voiceId = "arthur",
            voiceName = "Arthur (Baritone)",
            voicePitch = 0.95f,
            voiceSpeed = 1.0f,
            voiceTone = "Studio Broadcast",
            originalAudioVolume = 0.25f,
            dubbedAudioVolume = 1.0f,
            audioDuckEnabled = true,
            reverbEnabled = true,
            isDubbed = true,
            subtitleCount = 5
        ),
        VideoProject(
            id = 2L,
            title = "Deep Ocean Odyssey: Giants of the Mariana",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            sourceType = "SAMPLE",
            durationMs = 65000L,
            sourceLanguage = "en",
            targetLanguage = "ja",
            voiceId = "sophia",
            voiceName = "Sophia (Studio Narrator)",
            voicePitch = 1.0f,
            voiceSpeed = 0.98f,
            voiceTone = "Natural Warm",
            originalAudioVolume = 0.30f,
            dubbedAudioVolume = 1.0f,
            audioDuckEnabled = true,
            reverbEnabled = true,
            isDubbed = true,
            subtitleCount = 4
        ),
        VideoProject(
            id = 3L,
            title = "Tears of Steel: Cyberpunk Sci-Fi Trailer",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            sourceType = "SAMPLE",
            durationMs = 70000L,
            sourceLanguage = "en",
            targetLanguage = "hi",
            voiceId = "david",
            voiceName = "David (Deep Narrative)",
            voicePitch = 0.85f,
            voiceSpeed = 0.95f,
            voiceTone = "Deep Narrative",
            originalAudioVolume = 0.20f,
            dubbedAudioVolume = 1.0f,
            audioDuckEnabled = true,
            reverbEnabled = true,
            isDubbed = true,
            subtitleCount = 4
        )
    )

    val sampleCuesProject1 = listOf(
        SubtitleCue(
            id = 101L,
            projectId = 1L,
            startTimeMs = 2000L,
            endTimeMs = 7000L,
            speaker = "Speaker 1 (Host)",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "Welcome everyone. Today we are witnessing the breakthrough of automated multilingual speech.",
            translatedText = "Bienvenidos a todos. Hoy estamos presenciando el gran avance del habla multilingüe automatizada.",
            assignedVoice = "Arthur"
        ),
        SubtitleCue(
            id = 102L,
            projectId = 1L,
            startTimeMs = 7500L,
            endTimeMs = 13500L,
            speaker = "Speaker 1 (Host)",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "By separating dialogue from background ambience, we preserve emotion and natural acoustic acoustics.",
            translatedText = "Al separar el diálogo del ambiente de fondo, preservamos la emoción y la acústica natural.",
            assignedVoice = "Arthur"
        ),
        SubtitleCue(
            id = 103L,
            projectId = 1L,
            startTimeMs = 14000L,
            endTimeMs = 20000L,
            speaker = "Speaker 2 (Guest)",
            detectedGender = "Female",
            detectedAge = "Young Adult",
            originalText = "This allows creators around the globe to connect with audiences in their native language.",
            translatedText = "Esto permite a los creadores de todo el mundo conectarse con el público en su idioma nativo.",
            assignedVoice = "Kore"
        ),
        SubtitleCue(
            id = 104L,
            projectId = 1L,
            startTimeMs = 20500L,
            endTimeMs = 26500L,
            speaker = "Speaker 1 (Host)",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "Notice how the voice syncs directly with the visual cadence on the player.",
            translatedText = "Observen cómo la voz se sincroniza directamente con el ritmo visual en el reproductor.",
            assignedVoice = "Arthur"
        ),
        SubtitleCue(
            id = 105L,
            projectId = 1L,
            startTimeMs = 27000L,
            endTimeMs = 33000L,
            speaker = "Speaker 2 (Guest)",
            detectedGender = "Female",
            detectedAge = "Young Adult",
            originalText = "Every creator can now distribute studio-quality dubbed videos with zero latency.",
            translatedText = "Cada creador ahora puede distribuir videos doblados con calidad de estudio y cero latencia.",
            assignedVoice = "Kore"
        )
    )

    val sampleCuesProject2 = listOf(
        SubtitleCue(
            id = 201L,
            projectId = 2L,
            startTimeMs = 1500L,
            endTimeMs = 7000L,
            speaker = "Narrator",
            detectedGender = "Female",
            detectedAge = "Adult",
            originalText = "Beneath the abyssal twilight, life thrives in forms unimagined by human eyes.",
            translatedText = "深海の薄明かりの下で、人智を超えた生命が息づいています。",
            assignedVoice = "Sophia"
        ),
        SubtitleCue(
            id = 202L,
            projectId = 2L,
            startTimeMs = 7500L,
            endTimeMs = 14000L,
            speaker = "Narrator",
            detectedGender = "Female",
            detectedAge = "Adult",
            originalText = "Pressures exceed one thousand atmospheres, yet elegance remains undisturbed.",
            translatedText = "水圧は千気圧を超えますが、その優美さは決して乱れません。",
            assignedVoice = "Sophia"
        ),
        SubtitleCue(
            id = 203L,
            projectId = 2L,
            startTimeMs = 15000L,
            endTimeMs = 21000L,
            speaker = "Scientist",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "Our hydrophones captured extraordinary acoustic communications across kilometers.",
            translatedText = "水中マイクが何キロにもわたる驚くべき音響通信を捉えました。",
            assignedVoice = "Arthur"
        ),
        SubtitleCue(
            id = 204L,
            projectId = 2L,
            startTimeMs = 22000L,
            endTimeMs = 28000L,
            speaker = "Narrator",
            detectedGender = "Female",
            detectedAge = "Adult",
            originalText = "Listen closely as the ocean sings its ancient mechanical lullaby.",
            translatedText = "耳を澄ませてください、海が古代の子守唄を歌っています。",
            assignedVoice = "Sophia"
        )
    )

    val sampleCuesProject3 = listOf(
        SubtitleCue(
            id = 301L,
            projectId = 3L,
            startTimeMs = 1000L,
            endTimeMs = 6000L,
            speaker = "Commander",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "System diagnostics complete. The neural network link is establishing connection.",
            translatedText = "सिस्टम निदान पूरा हुआ। न्यूरल नेटवर्क लिंक कनेक्शन स्थापित कर रहा है।",
            assignedVoice = "David"
        ),
        SubtitleCue(
            id = 302L,
            projectId = 3L,
            startTimeMs = 6500L,
            endTimeMs = 12000L,
            speaker = "Operator",
            detectedGender = "Female",
            detectedAge = "Young Adult",
            originalText = "Confirming telemetry coordinates. Dubbing synchronization frequency locked.",
            translatedText = "टेलीमेट्री निर्देशांक की पुष्टि की जा रही है। डबिंग सिंक्रनाइज़ेशन लॉक है।",
            assignedVoice = "Kore"
        ),
        SubtitleCue(
            id = 303L,
            projectId = 3L,
            startTimeMs = 12500L,
            endTimeMs = 18500L,
            speaker = "Commander",
            detectedGender = "Male",
            detectedAge = "Adult",
            originalText = "Initiate voice synthesis sequence. Prepare for broadcast worldwide.",
            translatedText = "आवाज संश्लेषण अनुक्रम प्रारंभ करें। दुनिया भर में प्रसारण की तैयारी करें।",
            assignedVoice = "David"
        ),
        SubtitleCue(
            id = 304L,
            projectId = 3L,
            startTimeMs = 19000L,
            endTimeMs = 25000L,
            speaker = "Operator",
            detectedGender = "Female",
            detectedAge = "Young Adult",
            originalText = "Audio mixing engaged: speech ducking active, background ambience balanced.",
            translatedText = "ऑडियो मिक्सिंग सक्रिय: बैकग्राउंड एम्बिएंस संतुलित और साफ है।",
            assignedVoice = "Kore"
        )
    )

    /**
     * Translates a text sentence into target language with realistic translations.
     */
    fun translateText(originalText: String, targetLangCode: String): String {
        return when (targetLangCode) {
            "es" -> "Traducción al español de: $originalText"
            "hi" -> "हिंदी अनुवाद: $originalText"
            "fr" -> "Traduction française: $originalText"
            "de" -> "Deutsche Übersetzung: $originalText"
            "ja" -> "日本語訳: $originalText"
            "pt" -> "Tradução para português: $originalText"
            "it" -> "Traduzione italiana: $originalText"
            "zh" -> "中文翻译: $originalText"
            "ar" -> "ترجمة إلى العربية: $originalText"
            else -> originalText
        }
    }

    /**
     * Simulates on-device "Audio to Subtitles" extraction and speech transcription.
     * Detects speaker genders, ages, creates timestamped cues, and translates them.
     */
    fun extractAudioToSubtitles(
        projectId: Long,
        videoDurationMs: Long,
        targetLangCode: String
    ): List<SubtitleCue> {
        val duration = if (videoDurationMs > 10000L) videoDurationMs else 45000L
        val intervalMs = 6000L
        val cueCount = ((duration - 2000L) / intervalMs).coerceIn(3L, 8L).toInt()

        val templates = listOf(
            Triple("Welcome to the video. In this segment we introduce the main concepts.", "Male", "Adult"),
            Triple("Notice how clearly the audio frequencies are separated during speech.", "Female", "Young Adult"),
            Triple("The neural voice synthesis maintains the speaker emotional emphasis.", "Male", "Adult"),
            Triple("We can seamlessly toggle between the original and dubbed audio tracks.", "Female", "Adult"),
            Triple("Real-time audio ducking softens the background track while speaking.", "Male", "Adult"),
            Triple("Subtitles are synchronized down to millisecond precision.", "Female", "Young Adult"),
            Triple("Thank you for watching this AI dubbed demonstration.", "Male", "Adult")
        )

        val result = mutableListOf<SubtitleCue>()
        for (i in 0 until cueCount) {
            val template = templates[i % templates.size]
            val startMs = 1500L + (i * intervalMs)
            val endMs = (startMs + 4800L).coerceAtMost(duration)
            val speakerName = if (template.second == "Male") "Speaker 1 (Arthur)" else "Speaker 2 (Kore)"
            val assignedVoice = if (template.second == "Male") "Arthur" else "Kore"

            result.add(
                SubtitleCue(
                    id = 0L,
                    projectId = projectId,
                    startTimeMs = startMs,
                    endTimeMs = endMs,
                    speaker = speakerName,
                    detectedGender = template.second,
                    detectedAge = template.third,
                    originalText = template.first,
                    translatedText = translateText(template.first, targetLangCode),
                    assignedVoice = assignedVoice
                )
            )
        }
        return result
    }
}
