package com.example.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ImageSettings
import com.example.model.OutputFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_ACCENT_COLOR = intPreferencesKey("accent_color")
        val KEY_WORKER_COUNT = intPreferencesKey("worker_count")
        val KEY_BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val KEY_RECENT_SOURCE_URI = stringPreferencesKey("recent_source_uri")
        val KEY_RECENT_DEST_URI = stringPreferencesKey("recent_dest_uri")
        val KEY_AUTO_START = booleanPreferencesKey("auto_start_job")
        
        // Compression settings
        val KEY_MIN_QUALITY = intPreferencesKey("min_quality")
        val KEY_MAX_QUALITY = intPreferencesKey("max_quality")
        val KEY_MAX_ATTEMPTS = intPreferencesKey("max_attempts")
        val KEY_ALLOW_RESIZE = booleanPreferencesKey("allow_resize")
        val KEY_MAX_DOWNSCALE_PCT = intPreferencesKey("max_downscale_pct")
        val KEY_RESIZE_STEP_PCT = intPreferencesKey("resize_step_pct")
        val KEY_JPEG_OPTIMIZATION = booleanPreferencesKey("jpeg_opt")
        val KEY_PROGRESSIVE_JPEG = booleanPreferencesKey("progressive_jpeg")
        val KEY_REMOVE_METADATA = booleanPreferencesKey("remove_metadata")
        val KEY_PRESERVE_ICC = booleanPreferencesKey("preserve_icc")
        val KEY_DEFAULT_OUTPUT_FORMAT = stringPreferencesKey("default_output_format")
        val KEY_MAX_WIDTH = intPreferencesKey("max_width")
        val KEY_MAX_HEIGHT = intPreferencesKey("max_height")
        val KEY_MAINTAIN_ASPECT = booleanPreferencesKey("maintain_aspect")
        val KEY_DO_NOT_UPSCALE = booleanPreferencesKey("do_not_upscale")
    }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val themeName = prefs[KEY_THEME] ?: AppTheme.SYSTEM.name
        runCatching { AppTheme.valueOf(themeName) }.getOrDefault(AppTheme.SYSTEM)
    }

    val accentColorFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACCENT_COLOR] ?: 0 // 0 = Indigo, 1 = Cyan, 2 = Emerald, 3 = Rose, 4 = Amber
    }

    val biometricLockFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_BIOMETRIC_LOCK] ?: false
    }

    val recentSourceUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_RECENT_SOURCE_URI]
    }

    val recentDestUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_RECENT_DEST_URI]
    }

    val imageSettingsFlow: Flow<ImageSettings> = context.dataStore.data.map { prefs ->
        ImageSettings(
            minQuality = prefs[KEY_MIN_QUALITY] ?: 15,
            maxQuality = prefs[KEY_MAX_QUALITY] ?: 95,
            maxAttempts = prefs[KEY_MAX_ATTEMPTS] ?: 10,
            allowResize = prefs[KEY_ALLOW_RESIZE] ?: true,
            maxDownscalePercentage = prefs[KEY_MAX_DOWNSCALE_PCT] ?: 50,
            resizeStepPercentage = prefs[KEY_RESIZE_STEP_PCT] ?: 10,
            jpegOptimization = prefs[KEY_JPEG_OPTIMIZATION] ?: true,
            progressiveJpeg = prefs[KEY_PROGRESSIVE_JPEG] ?: true,
            removeMetadata = prefs[KEY_REMOVE_METADATA] ?: false,
            preserveIccProfile = prefs[KEY_PRESERVE_ICC] ?: true,
            defaultOutputFormat = runCatching {
                OutputFormat.valueOf(prefs[KEY_DEFAULT_OUTPUT_FORMAT] ?: OutputFormat.JPEG.name)
            }.getOrDefault(OutputFormat.JPEG),
            maxWidth = prefs[KEY_MAX_WIDTH] ?: 2048,
            maxHeight = prefs[KEY_MAX_HEIGHT] ?: 2048,
            maintainAspectRatio = prefs[KEY_MAINTAIN_ASPECT] ?: true,
            doNotUpscale = prefs[KEY_DO_NOT_UPSCALE] ?: true,
            workerCount = prefs[KEY_WORKER_COUNT] ?: 2
        )
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = theme.name
        }
    }

    suspend fun setAccentColor(index: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCENT_COLOR] = index
        }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BIOMETRIC_LOCK] = enabled
        }
    }

    suspend fun setRecentFolders(sourceUri: String?, destUri: String?) {
        context.dataStore.edit { prefs ->
            if (sourceUri != null) prefs[KEY_RECENT_SOURCE_URI] = sourceUri
            if (destUri != null) prefs[KEY_RECENT_DEST_URI] = destUri
        }
    }

    suspend fun updateImageSettings(settings: ImageSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MIN_QUALITY] = settings.minQuality
            prefs[KEY_MAX_QUALITY] = settings.maxQuality
            prefs[KEY_MAX_ATTEMPTS] = settings.maxAttempts
            prefs[KEY_ALLOW_RESIZE] = settings.allowResize
            prefs[KEY_MAX_DOWNSCALE_PCT] = settings.maxDownscalePercentage
            prefs[KEY_RESIZE_STEP_PCT] = settings.resizeStepPercentage
            prefs[KEY_JPEG_OPTIMIZATION] = settings.jpegOptimization
            prefs[KEY_PROGRESSIVE_JPEG] = settings.progressiveJpeg
            prefs[KEY_REMOVE_METADATA] = settings.removeMetadata
            prefs[KEY_PRESERVE_ICC] = settings.preserveIccProfile
            prefs[KEY_DEFAULT_OUTPUT_FORMAT] = settings.defaultOutputFormat.name
            prefs[KEY_MAX_WIDTH] = settings.maxWidth
            prefs[KEY_MAX_HEIGHT] = settings.maxHeight
            prefs[KEY_MAINTAIN_ASPECT] = settings.maintainAspectRatio
            prefs[KEY_DO_NOT_UPSCALE] = settings.doNotUpscale
            prefs[KEY_WORKER_COUNT] = settings.workerCount
        }
    }
}
