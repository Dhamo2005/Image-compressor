package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.model.CompressionJob
import com.example.model.LogItem
import com.example.model.LogType
import com.example.repository.CompressionRepository
import com.example.storage.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LogFilter {
    ALL,
    SUCCESS,
    WARNING,
    ERROR
}

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val compressionRepository = CompressionRepository(
        context = application,
        jobDao = db.compressionJobDao(),
        logDao = db.logDao()
    )

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(LogFilter.ALL)

    val jobs: StateFlow<List<CompressionJob>> = compressionRepository.allJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredLogs: StateFlow<List<LogItem>> = combine(
        compressionRepository.allLogs,
        searchQuery,
        selectedFilter
    ) { logs, query, filter ->
        logs.filter { item ->
            val matchesQuery = query.isEmpty() ||
                    item.studentName.contains(query, ignoreCase = true) ||
                    item.fileName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                LogFilter.ALL -> true
                LogFilter.SUCCESS -> item.logType == LogType.SUCCESS
                LogFilter.WARNING -> item.logType == LogType.WARNING
                LogFilter.ERROR -> item.logType == LogType.ERROR
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun clearHistory() {
        viewModelScope.launch {
            compressionRepository.clearHistory()
        }
    }

    fun generateCsvReport(): String {
        val sb = StringBuilder()
        sb.append("Timestamp,Student Name,File Name,Log Type,Original Size (Bytes),Compressed Size (Bytes),Space Saved (Bytes),Message\n")
        filteredLogs.value.forEach { item ->
            val saved = (item.originalSize - item.compressedSize).coerceAtLeast(0)
            sb.append("\"${item.timestamp}\",\"${item.studentName}\",\"${item.fileName}\",\"${item.logType}\",${item.originalSize},${item.compressedSize},$saved,\"${item.message.replace("\"", "\"\"")}\"\n")
        }
        return sb.toString()
    }
}
