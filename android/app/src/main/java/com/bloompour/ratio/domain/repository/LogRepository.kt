package com.bloompour.ratio.domain.repository

import com.bloompour.ratio.domain.model.BrewLogEntry
import kotlinx.coroutines.flow.StateFlow

interface LogRepository {
    val entries: StateFlow<List<BrewLogEntry>>
    fun add(entry: BrewLogEntry)
    fun clear()
}
