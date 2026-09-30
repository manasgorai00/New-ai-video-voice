package com.example.data

import com.example.data.dao.VideoDubDao
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideoDubRepository(private val dao: VideoDubDao) {

    val allProjects: Flow<List<VideoProject>> = dao.getAllProjects()

    fun getProject(id: Long): Flow<VideoProject?> = dao.getProjectById(id)

    suspend fun getProjectDirect(id: Long): VideoProject? = dao.getProjectByIdDirect(id)

    fun getCuesForProject(projectId: Long): Flow<List<SubtitleCue>> = dao.getCuesForProject(projectId)

    suspend fun getCuesSync(projectId: Long): List<SubtitleCue> = dao.getCuesForProjectSync(projectId)

    suspend fun initializeSampleDataIfEmpty() {
        withContext(Dispatchers.IO) {
            val count = dao.countProjects()
            if (count == 0) {
                for (project in SampleDataProvider.sampleProjects) {
                    val newId = dao.insertProject(project)
                    val cues = when (project.id) {
                        1L -> SampleDataProvider.sampleCuesProject1.map { it.copy(id = 0L, projectId = newId) }
                        2L -> SampleDataProvider.sampleCuesProject2.map { it.copy(id = 0L, projectId = newId) }
                        3L -> SampleDataProvider.sampleCuesProject3.map { it.copy(id = 0L, projectId = newId) }
                        else -> emptyList()
                    }
                    if (cues.isNotEmpty()) {
                        dao.insertCues(cues)
                    }
                }
            }
        }
    }

    suspend fun createProject(
        title: String,
        videoUrl: String,
        sourceType: String,
        sourceLanguage: String = "en",
        targetLanguage: String = "es",
        voiceId: String = "arthur"
    ): Long = withContext(Dispatchers.IO) {
        val voice = SampleDataProvider.getVoiceById(voiceId)
        val project = VideoProject(
            title = title,
            videoUrl = videoUrl,
            sourceType = sourceType,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage,
            voiceId = voice.id,
            voiceName = "${voice.name} (${voice.gender})",
            voicePitch = voice.defaultPitch,
            voiceSpeed = voice.defaultSpeed,
            voiceTone = voice.toneProfile,
            originalAudioVolume = 0.25f,
            dubbedAudioVolume = 1.0f,
            audioDuckEnabled = true,
            reverbEnabled = true,
            isDubbed = false,
            subtitleCount = 0
        )
        val newId = dao.insertProject(project)

        // Generate initial subtitle cues using Audio-to-Subtitles extractor
        val cues = SampleDataProvider.extractAudioToSubtitles(
            projectId = newId,
            videoDurationMs = 50000L,
            targetLangCode = targetLanguage
        )
        dao.insertCues(cues)
        dao.updateProject(project.copy(id = newId, subtitleCount = cues.size, isDubbed = true))

        newId
    }

    suspend fun updateProject(project: VideoProject) = withContext(Dispatchers.IO) {
        dao.updateProject(project)
    }

    suspend fun deleteProject(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteCuesForProject(id)
        dao.deleteProject(id)
    }

    suspend fun updateVoiceMode(
        projectId: Long,
        voiceId: String,
        pitch: Float,
        speed: Float,
        tone: String
    ) = withContext(Dispatchers.IO) {
        val project = dao.getProjectByIdDirect(projectId) ?: return@withContext
        val voice = SampleDataProvider.getVoiceById(voiceId)
        val updated = project.copy(
            voiceId = voiceId,
            voiceName = "${voice.name} (${voice.gender})",
            voicePitch = pitch,
            voiceSpeed = speed,
            voiceTone = tone
        )
        dao.updateProject(updated)
    }

    suspend fun updateAudioMix(
        projectId: Long,
        origVolume: Float,
        dubVolume: Float,
        duckEnabled: Boolean,
        reverbEnabled: Boolean
    ) = withContext(Dispatchers.IO) {
        val project = dao.getProjectByIdDirect(projectId) ?: return@withContext
        val updated = project.copy(
            originalAudioVolume = origVolume,
            dubbedAudioVolume = dubVolume,
            audioDuckEnabled = duckEnabled,
            reverbEnabled = reverbEnabled
        )
        dao.updateProject(updated)
    }

    suspend fun updateTargetLanguage(projectId: Long, newTargetLang: String) = withContext(Dispatchers.IO) {
        val project = dao.getProjectByIdDirect(projectId) ?: return@withContext
        val currentCues = dao.getCuesForProjectSync(projectId)
        val updatedCues = currentCues.map { cue ->
            cue.copy(translatedText = SampleDataProvider.translateText(cue.originalText, newTargetLang))
        }
        dao.deleteCuesForProject(projectId)
        dao.insertCues(updatedCues)
        dao.updateProject(project.copy(targetLanguage = newTargetLang, isDubbed = true))
    }

    suspend fun runAudioToSubtitlesExtraction(projectId: Long) = withContext(Dispatchers.IO) {
        val project = dao.getProjectByIdDirect(projectId) ?: return@withContext
        val cues = SampleDataProvider.extractAudioToSubtitles(
            projectId = projectId,
            videoDurationMs = if (project.durationMs > 0) project.durationMs else 50000L,
            targetLangCode = project.targetLanguage
        )
        dao.deleteCuesForProject(projectId)
        dao.insertCues(cues)
        dao.updateProject(project.copy(subtitleCount = cues.size, isDubbed = true))
    }

    suspend fun saveCue(cue: SubtitleCue) = withContext(Dispatchers.IO) {
        if (cue.id == 0L) {
            dao.insertCue(cue)
        } else {
            dao.updateCue(cue)
        }
    }

    suspend fun deleteCue(cueId: Long, projectId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCue(cueId)
        val remaining = dao.getCuesForProjectSync(projectId)
        val project = dao.getProjectByIdDirect(projectId)
        if (project != null) {
            dao.updateProject(project.copy(subtitleCount = remaining.size))
        }
    }
}
