package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.DocumentRule
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentRuleDao {
    @Query("SELECT * FROM document_rules ORDER BY name ASC")
    fun getAllRules(): Flow<List<DocumentRule>>

    @Query("SELECT * FROM document_rules WHERE enabled = 1")
    suspend fun getEnabledRulesList(): List<DocumentRule>

    @Query("SELECT * FROM document_rules WHERE id = :id")
    suspend fun getRuleById(id: String): DocumentRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: DocumentRule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<DocumentRule>)

    @Update
    suspend fun updateRule(rule: DocumentRule)

    @Delete
    suspend fun deleteRule(rule: DocumentRule)

    @Query("DELETE FROM document_rules")
    suspend fun deleteAllRules()
}
