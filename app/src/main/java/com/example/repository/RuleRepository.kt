package com.example.repository

import com.example.database.DocumentRuleDao
import com.example.database.ProfileDao
import com.example.model.CompressionProfile
import com.example.model.DocumentRule
import com.example.model.OutputFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RuleRepository(
    private val ruleDao: DocumentRuleDao,
    private val profileDao: ProfileDao
) {
    val allRules: Flow<List<DocumentRule>> = ruleDao.getAllRules()
    val allProfiles: Flow<List<CompressionProfile>> = profileDao.getAllProfiles()

    suspend fun initializeDefaultRulesIfNeeded() {
        val existing = ruleDao.getAllRules().first()
        if (existing.isEmpty()) {
            val defaultRules = listOf(
                DocumentRule(name = "Marksheet", targetSizeKb = 296, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Aadhar", targetSizeKb = 296, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Signature", targetSizeKb = 146, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Typewriting", targetSizeKb = 296, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Community Certificate", targetSizeKb = 296, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Income Certificate", targetSizeKb = 296, outputFormat = OutputFormat.JPEG, enabled = true),
                DocumentRule(name = "Passport Photo", targetSizeKb = 96, outputFormat = OutputFormat.JPEG, enabled = true)
            )
            ruleDao.insertRules(defaultRules)
        }

        val existingProfiles = profileDao.getAllProfiles().first()
        if (existingProfiles.isEmpty()) {
            val collegeProfile = CompressionProfile(
                name = "College Admission",
                description = "Default standard student document sizes (Marksheet 296KB, Signature 146KB, Aadhar 296KB)",
                rulesJson = "",
                isDefault = true
            )
            val govProfile = CompressionProfile(
                name = "Government Exam / UPSC",
                description = "Strict ultra-compressed specifications (Photo <50KB, Signature <20KB, Certificates <200KB)",
                rulesJson = "",
                isDefault = false
            )
            val passportProfile = CompressionProfile(
                name = "Passport & Visa Application",
                description = "High resolution passport size specifications",
                rulesJson = "",
                isDefault = false
            )
            profileDao.insertProfile(collegeProfile)
            profileDao.insertProfile(govProfile)
            profileDao.insertProfile(passportProfile)
        }
    }

    suspend fun insertRule(rule: DocumentRule) = ruleDao.insertRule(rule)
    suspend fun updateRule(rule: DocumentRule) = ruleDao.updateRule(rule)
    suspend fun deleteRule(rule: DocumentRule) = ruleDao.deleteRule(rule)
    suspend fun insertProfile(profile: CompressionProfile) = profileDao.insertProfile(profile)
    suspend fun deleteProfile(profile: CompressionProfile) = profileDao.deleteProfile(profile)
}
