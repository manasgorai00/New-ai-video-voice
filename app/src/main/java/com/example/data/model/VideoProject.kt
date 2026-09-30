package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val videoUrl: String,
    val sourceType: String, // "LINK", "YOUTUBE", "GDRIVE", "LOCAL", "SAMPLE"
    val durationMs: Long = 0L,
    val sourceLanguage: String = "en",
    val targetLanguage: String = "es",
    val voiceId: String = "arthur",
    val voiceName: String = "Arthur (Baritone)",
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val voiceTone: String = "Natural Warm",
    val originalAudioVolume: Float = 0.25f,
    val dubbedAudioVolume: Float = 1.0f,
    val audioDuckEnabled: Boolean = true,
    val reverbEnabled: Boolean = true,
    val isDubbed: Boolean = false,
    val subtitleCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
