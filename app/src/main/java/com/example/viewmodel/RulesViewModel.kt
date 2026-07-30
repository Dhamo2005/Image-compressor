package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.model.CompressionProfile
import com.example.model.DocumentRule
import com.example.model.OutputFormat
import com.example.repository.RuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class RulesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val ruleRepository = RuleRepository(
        ruleDao = db.documentRuleDao(),
        profileDao = db.profileDao()
    )

    val rules: StateFlow<List<DocumentRule>> = ruleRepository.allRules
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val profiles: StateFlow<List<CompressionProfile>> = ruleRepository.allProfiles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            ruleRepository.initializeDefaultRulesIfNeeded()
        }
    }

    fun addRule(rule: DocumentRule) {
        viewModelScope.launch {
            ruleRepository.insertRule(rule)
        }
    }

    fun updateRule(rule: DocumentRule) {
        viewModelScope.launch {
            ruleRepository.updateRule(rule)
        }
    }

    fun deleteRule(rule: DocumentRule) {
        viewModelScope.launch {
            ruleRepository.deleteRule(rule)
        }
    }

    fun duplicateRule(rule: DocumentRule) {
        viewModelScope.launch {
            val copy = rule.copy(
                id = java.util.UUID.randomUUID().toString(),
                name = "${rule.name} (Copy)"
            )
            ruleRepository.insertRule(copy)
        }
    }

    fun toggleRuleEnabled(rule: DocumentRule) {
        viewModelScope.launch {
            ruleRepository.updateRule(rule.copy(enabled = !rule.enabled))
        }
    }

    fun exportRulesJson(): String {
        val currentRules = rules.value
        val jsonArray = JSONArray()
        currentRules.forEach { r ->
            val obj = JSONObject().apply {
                put("name", r.name)
                put("targetSizeKb", r.targetSizeKb)
                put("outputFormat", r.outputFormat.name)
                put("enabled", r.enabled)
                put("maxQuality", r.maxQuality)
                put("minQuality", r.minQuality)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importRulesJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val array = JSONArray(jsonString)
                val importedList = mutableListOf<DocumentRule>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val rule = DocumentRule(
                        name = obj.optString("name", "Custom Rule"),
                        targetSizeKb = obj.optInt("targetSizeKb", 296),
                        outputFormat = runCatching { OutputFormat.valueOf(obj.optString("outputFormat", "JPEG")) }.getOrDefault(OutputFormat.JPEG),
                        enabled = obj.optBoolean("enabled", true),
                        maxQuality = obj.optInt("maxQuality", 95),
                        minQuality = obj.optInt("minQuality", 20)
                    )
                    importedList.add(rule)
                }
                if (importedList.isNotEmpty()) {
                    importedList.forEach { ruleRepository.insertRule(it) }
                }
            } catch (e: Exception) {
                // Parse error
            }
        }
    }

    fun saveProfile(name: String, description: String) {
        viewModelScope.launch {
            val json = exportRulesJson()
            val profile = CompressionProfile(
                name = name,
                description = description,
                rulesJson = json
            )
            ruleRepository.insertProfile(profile)
        }
    }

    fun deleteProfile(profile: CompressionProfile) {
        viewModelScope.launch {
            ruleRepository.deleteProfile(profile)
        }
    }
}
