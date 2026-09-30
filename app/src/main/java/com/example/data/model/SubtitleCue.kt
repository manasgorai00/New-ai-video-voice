package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subtitle_cues")
data class SubtitleCue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val speaker: String = "Speaker 1",
    val detectedGender: String = "Male",
    val detectedAge: String = "Adult",
    val originalText: String,
    val translatedText: String,
    val assignedVoice: String = "Arthur"
)
