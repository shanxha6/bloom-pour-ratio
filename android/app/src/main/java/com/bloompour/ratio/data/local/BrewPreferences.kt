package com.bloompour.ratio.data.local

import android.content.Context

class BrewPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun getString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getFloat(key: String, fallback: Float): Float = prefs.getFloat(key, fallback)

    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun getInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun has(key: String): Boolean = prefs.contains(key)

    companion object {
        private const val FILE_NAME = "bloom_pour_ratio_prefs"

        const val KEY_RECIPES = "recipes"
        const val KEY_LOG = "log"
        const val KEY_BREW_COUNT = "brew_count"
        const val KEY_LAST_RATIO = "last_ratio"
        const val KEY_LAST_DOSE = "last_dose"
        const val KEY_RATIO_TALLY = "ratio_tally"
        const val KEY_UNITS_OUNCES = "units_ounces"
        const val KEY_DEFAULT_RATIO = "default_ratio"
        const val KEY_BLOOM_MULTIPLIER = "bloom_multiplier"
        const val KEY_STAGE_ALERTS = "stage_alerts"
        const val KEY_LIGHT_THEME = "light_theme"
    }
}
