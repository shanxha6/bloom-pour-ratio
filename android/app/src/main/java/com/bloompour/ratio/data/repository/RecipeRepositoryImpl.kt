package com.bloompour.ratio.data.repository

import com.bloompour.ratio.core.config.GameConfig
import com.bloompour.ratio.data.local.BrewPreferences
import com.bloompour.ratio.data.sample.SampleData
import com.bloompour.ratio.domain.model.Recipe
import com.bloompour.ratio.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

private const val FIELD_SEPARATOR = "|"
private const val ENTRY_SEPARATOR = ";"
private const val PAIR_SEPARATOR = ":"

class RecipeRepositoryImpl(private val preferences: BrewPreferences) : RecipeRepository {

    private val _recipes = MutableStateFlow(loadRecipes())
    override val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _brewCount = MutableStateFlow(preferences.getInt(BrewPreferences.KEY_BREW_COUNT, 0))
    override val brewCount: StateFlow<Int> = _brewCount.asStateFlow()

    override fun saveRecipe(recipe: Recipe) {
        val current = _recipes.value.filterNot { it.name == recipe.name }
        val updated = (listOf(recipe) + current).take(GameConfig.RECIPE_CAPACITY)
        _recipes.value = updated
        persist(updated)
    }

    override fun registerBrew(ratio: Float, dose: Float) {
        val next = _brewCount.value + 1
        _brewCount.value = next
        preferences.putInt(BrewPreferences.KEY_BREW_COUNT, next)
        preferences.putFloat(BrewPreferences.KEY_LAST_RATIO, ratio)
        preferences.putFloat(BrewPreferences.KEY_LAST_DOSE, dose)
        bumpTally(ratio)
    }

    override fun lastRatio(): Float = preferences.getFloat(BrewPreferences.KEY_LAST_RATIO, 16.0f)

    override fun lastDose(): Float = preferences.getFloat(BrewPreferences.KEY_LAST_DOSE, 20.0f)

    override fun mostUsedRatio(): Float {
        val best = readTally().maxByOrNull { it.value }
        return if (best == null) 0f else best.key / 2f
    }

    private fun bumpTally(ratio: Float) {
        val key = (ratio * 2f).roundToInt()
        val tally = readTally().toMutableMap()
        tally[key] = (tally[key] ?: 0) + 1
        val raw = tally.entries.joinToString(ENTRY_SEPARATOR) { entry ->
            entry.key.toString() + PAIR_SEPARATOR + entry.value.toString()
        }
        preferences.putString(BrewPreferences.KEY_RATIO_TALLY, raw)
    }

    private fun readTally(): Map<Int, Int> {
        val raw = preferences.getString(BrewPreferences.KEY_RATIO_TALLY, "")
        val result = mutableMapOf<Int, Int>()
        raw.split(ENTRY_SEPARATOR).forEach { chunk ->
            val pair = chunk.split(PAIR_SEPARATOR)
            if (pair.size == 2) {
                val key = pair[0].toIntOrNull()
                val count = pair[1].toIntOrNull()
                if (key != null && count != null) {
                    result[key] = count
                }
            }
        }
        return result
    }

    private fun loadRecipes(): List<Recipe> {
        if (!preferences.has(BrewPreferences.KEY_RECIPES)) {
            persist(SampleData.recipes)
            return SampleData.recipes
        }
        val parsed = preferences.getString(BrewPreferences.KEY_RECIPES, "")
            .split(ENTRY_SEPARATOR)
            .mapNotNull { decode(it) }
        return if (parsed.isEmpty()) SampleData.recipes else parsed
    }

    private fun persist(recipes: List<Recipe>) {
        preferences.putString(
            BrewPreferences.KEY_RECIPES,
            recipes.joinToString(ENTRY_SEPARATOR) { encode(it) }
        )
    }

    private fun encode(recipe: Recipe): String = listOf(
        recipe.name,
        recipe.ratio.toString(),
        recipe.dose.toString(),
        recipe.grind.toString(),
        if (recipe.dialledIn) "1" else "0"
    ).joinToString(FIELD_SEPARATOR)

    private fun decode(raw: String): Recipe? {
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size < 5) return null
        val ratio = parts[1].toFloatOrNull() ?: return null
        val dose = parts[2].toFloatOrNull() ?: return null
        val grind = parts[3].toIntOrNull() ?: return null
        return Recipe(
            name = parts[0],
            ratio = ratio,
            dose = dose,
            grind = grind,
            dialledIn = parts[4] == "1"
        )
    }
}
