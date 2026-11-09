package com.bloompour.ratio.data.repository

import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.data.local.BrewPreferences
import com.bloompour.ratio.data.sample.SampleData
import com.bloompour.ratio.domain.model.BrewLogEntry
import com.bloompour.ratio.domain.model.Verdict
import com.bloompour.ratio.domain.repository.LogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

private const val FIELD_SEPARATOR = "|"
private const val ENTRY_SEPARATOR = ";"

class LogRepositoryImpl(private val preferences: BrewPreferences) : LogRepository {

    private val _entries = MutableStateFlow(loadEntries())
    override val entries: StateFlow<List<BrewLogEntry>> = _entries.asStateFlow()

    override fun add(entry: BrewLogEntry) {
        val updated = (listOf(entry) + _entries.value).take(GameConfig.LOG_CAPACITY)
        _entries.value = updated
        persist(updated)
    }

    override fun clear() {
        _entries.value = emptyList()
        preferences.putString(BrewPreferences.KEY_LOG, "")
    }

    private fun loadEntries(): List<BrewLogEntry> {
        if (!preferences.has(BrewPreferences.KEY_LOG)) {
            val seed = SampleData.seedLog(LocalDate.now().toEpochDay())
            persist(seed)
            return seed
        }
        return preferences.getString(BrewPreferences.KEY_LOG, "")
            .split(ENTRY_SEPARATOR)
            .mapNotNull { decode(it) }
    }

    private fun persist(entries: List<BrewLogEntry>) {
        preferences.putString(
            BrewPreferences.KEY_LOG,
            entries.joinToString(ENTRY_SEPARATOR) { encode(it) }
        )
    }

    private fun encode(entry: BrewLogEntry): String = listOf(
        entry.epochDay.toString(),
        entry.recipeName,
        entry.ratio.toString(),
        entry.dose.toString(),
        entry.verdict.name
    ).joinToString(FIELD_SEPARATOR)

    private fun decode(raw: String): BrewLogEntry? {
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size < 5) return null
        val day = parts[0].toLongOrNull() ?: return null
        val ratio = parts[2].toFloatOrNull() ?: return null
        val dose = parts[3].toFloatOrNull() ?: return null
        val verdict = Verdict.entries.firstOrNull { it.name == parts[4] } ?: return null
        return BrewLogEntry(
            epochDay = day,
            recipeName = parts[1],
            ratio = ratio,
            dose = dose,
            verdict = verdict
        )
    }
}
