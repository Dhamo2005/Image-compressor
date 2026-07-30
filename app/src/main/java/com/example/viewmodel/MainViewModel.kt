package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

enum class BottomTab {
    COMPRESS,
    RULES,
    HISTORY,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val currentTab = MutableStateFlow(BottomTab.COMPRESS)
    val showAboutDialog = MutableStateFlow(false)
    val showHelpTutorial = MutableStateFlow(false)
    val showExportDialog = MutableStateFlow(false)
    val showProfilesDialog = MutableStateFlow(false)
    val showCameraScanner = MutableStateFlow(false)

    fun selectTab(tab: BottomTab) {
        currentTab.value = tab
    }
}
