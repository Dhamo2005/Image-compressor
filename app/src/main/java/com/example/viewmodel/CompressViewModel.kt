package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.model.CompressionJob
import com.example.model.LogItem
import com.example.preferences.AppPreferences
import com.example.repository.CompressionRepository
import com.example.storage.FileUtils
import com.example.storage.StorageHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CompressViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val compressionRepository = CompressionRepository(
        context = application,
        jobDao = db.compressionJobDao(),
        logDao = db.logDao()
    )
    private val prefs = AppPreferences(application)

    val sourceUri = MutableStateFlow<String?>(null)
    val destUri = MutableStateFlow<String?>(null)
    val sourceDisplayName = MutableStateFlow<String>("Select Source Folder")
    val destDisplayName = MutableStateFlow<String>("Select Output Folder")

    val activeJobId = MutableStateFlow<String?>(null)

    val activeJob: StateFlow<CompressionJob?> = activeJobId
        .flatMapLatest { jobId ->
            if (jobId == null) flowOf(null)
            else compressionRepository.observeJob(jobId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val liveLogs: StateFlow<List<LogItem>> = activeJobId
        .flatMapLatest { jobId ->
            if (jobId == null) flowOf(emptyList())
            else compressionRepository.getLogsForJob(jobId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            val src = prefs.recentSourceUriFlow.first()
            val dst = prefs.recentDestUriFlow.first()
            if (!src.isNullOrEmpty()) {
                sourceUri.value = src
                sourceDisplayName.value = FileUtils.getDisplayNameFromUri(application, Uri.parse(src))
            }
            if (!dst.isNullOrEmpty()) {
                destUri.value = dst
                destDisplayName.value = FileUtils.getDisplayNameFromUri(application, Uri.parse(dst))
            }
            compressionRepository.allJobs.first().maxByOrNull { it.startTime }?.let { latestJob ->
                if (activeJobId.value == null) {
                    activeJobId.value = latestJob.id
                }
            }
        }
    }

    fun setSourceFolder(uri: Uri) {
        val uriStr = uri.toString()
        sourceUri.value = uriStr
        sourceDisplayName.value = FileUtils.getDisplayNameFromUri(getApplication(), uri)
        viewModelScope.launch {
            prefs.setRecentFolders(sourceUri = uriStr, destUri = null)
        }
    }

    fun setDestFolder(uri: Uri) {
        val uriStr = uri.toString()
        destUri.value = uriStr
        destDisplayName.value = FileUtils.getDisplayNameFromUri(getApplication(), uri)
        viewModelScope.launch {
            prefs.setRecentFolders(sourceUri = null, destUri = uriStr)
        }
    }

    fun createSampleFoldersAndSelect() {
        viewModelScope.launch {
            val sampleUri = StorageHelper.createSampleStudentFolders(getApplication())
            setSourceFolder(sampleUri)
            setDestFolder(sampleUri)
        }
    }

    fun startCompression() {
        val src = sourceUri.value ?: return
        val dst = destUri.value ?: return
        viewModelScope.launch {
            val jobId = compressionRepository.startBatchCompression(src, dst)
            activeJobId.value = jobId
        }
    }

    fun cancelActiveJob() {
        val jobId = activeJobId.value ?: return
        viewModelScope.launch {
            compressionRepository.cancelJob(jobId)
        }
    }

    fun clearLiveLogs() {
        viewModelScope.launch {
            compressionRepository.clearLogs()
        }
    }
}
