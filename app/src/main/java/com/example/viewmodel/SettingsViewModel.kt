package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ImageSettings
import com.example.preferences.AppPreferences
import com.example.preferences.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)

    val theme: StateFlow<AppTheme> = prefs.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    val accentColorIndex: StateFlow<Int> = prefs.accentColorFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val biometricLock: StateFlow<Boolean> = prefs.biometricLockFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val imageSettings: StateFlow<ImageSettings> = prefs.imageSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ImageSettings()
        )

    fun setTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            prefs.setTheme(appTheme)
        }
    }

    fun setAccentColor(index: Int) {
        viewModelScope.launch {
            prefs.setAccentColor(index)
        }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setBiometricLock(enabled)
        }
    }

    fun updateImageSettings(settings: ImageSettings) {
        viewModelScope.launch {
            prefs.updateImageSettings(settings)
        }
    }
}
