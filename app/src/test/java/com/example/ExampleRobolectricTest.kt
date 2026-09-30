package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SampleDataProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VoxDub AI - Video Dubber", appName)
    }

    @Test
    fun testVoiceProfilesAvailable() {
        val voices = SampleDataProvider.availableVoices
        assertTrue("Voices should include Arthur and Kore", voices.any { it.name == "Arthur" } && voices.any { it.name == "Kore" })
        val arthur = SampleDataProvider.getVoiceById("arthur")
        assertEquals("Male", arthur.gender)
        assertEquals("Natural Warm", arthur.toneProfile)
    }

    @Test
    fun testAudioToSubtitlesExtraction() {
        val cues = SampleDataProvider.extractAudioToSubtitles(
            projectId = 999L,
            videoDurationMs = 60000L,
            targetLangCode = "es"
        )
        assertTrue("Cues should be generated with timestamp segments", cues.isNotEmpty())
        assertTrue("Cue should have speaker detection", cues.all { it.detectedGender.isNotBlank() })
        assertTrue("Cues should have valid start and end times", cues.all { it.endTimeMs > it.startTimeMs })
    }

    @Test
    fun testTranslationSupport() {
        val translated = SampleDataProvider.translateText("Hello World", "es")
        assertNotNull(translated)
        assertTrue(translated.contains("Hello World"))
    }
}
