package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.model.DashboardStats
import com.example.repository.CompressionRepository
import com.example.repository.RuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val compressionRepository = CompressionRepository(
        context = application,
        jobDao = db.compressionJobDao(),
        logDao = db.logDao()
    )
    private val ruleRepository = RuleRepository(
        ruleDao = db.documentRuleDao(),
        profileDao = db.profileDao()
    )

    val stats: StateFlow<DashboardStats> = compressionRepository.dashboardStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardStats()
        )

    val recentJobs = compressionRepository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentLogs = compressionRepository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearStats() {
        viewModelScope.launch {
            compressionRepository.clearHistory()
        }
    }

    init {
        viewModelScope.launch {
            ruleRepository.initializeDefaultRulesIfNeeded()
        }
    }
}
