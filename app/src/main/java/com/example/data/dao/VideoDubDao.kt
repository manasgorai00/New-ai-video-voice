package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SubtitleCue
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDubDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    fun getProjectById(id: Long): Flow<VideoProject?>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    suspend fun getProjectByIdDirect(id: Long): VideoProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProject): Long

    @Update
    suspend fun updateProject(project: VideoProject)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Query("SELECT * FROM subtitle_cues WHERE projectId = :projectId ORDER BY startTimeMs ASC")
    fun getCuesForProject(projectId: Long): Flow<List<SubtitleCue>>

    @Query("SELECT * FROM subtitle_cues WHERE projectId = :projectId ORDER BY startTimeMs ASC")
    suspend fun getCuesForProjectSync(projectId: Long): List<SubtitleCue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCue(cue: SubtitleCue): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCues(cues: List<SubtitleCue>)

    @Update
    suspend fun updateCue(cue: SubtitleCue)

    @Query("DELETE FROM subtitle_cues WHERE id = :id")
    suspend fun deleteCue(id: Long)

    @Query("DELETE FROM subtitle_cues WHERE projectId = :projectId")
    suspend fun deleteCuesForProject(projectId: Long)

    @Query("SELECT COUNT(*) FROM video_projects")
    suspend fun countProjects(): Int
}
