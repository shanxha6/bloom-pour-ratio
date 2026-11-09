package com.bloompour.ratio.data.sample

import com.bloompour.ratio.domain.model.BrewLogEntry
import com.bloompour.ratio.domain.model.Recipe
import com.bloompour.ratio.domain.model.Verdict

object SampleData {

    const val DEFAULT_RECIPE_NAME = "HOUSE POUR"

    val recipes: List<Recipe> = listOf(
        Recipe(name = "MORNING V60", ratio = 16.0f, dose = 18.0f, grind = 11, dialledIn = true),
        Recipe(name = "WEEKEND CHEMEX", ratio = 17.0f, dose = 30.0f, grind = 14, dialledIn = false),
        Recipe(name = "QUICK CUP", ratio = 15.0f, dose = 14.0f, grind = 10, dialledIn = false),
        Recipe(name = "LIGHT ROAST BLOOM", ratio = 18.0f, dose = 20.0f, grind = 9, dialledIn = false)
    )

    fun seedLog(today: Long): List<BrewLogEntry> = listOf(
        BrewLogEntry(
            epochDay = today - 1,
            recipeName = "MORNING V60",
            ratio = 16.0f,
            dose = 18.0f,
            verdict = Verdict.BALANCED
        ),
        BrewLogEntry(
            epochDay = today - 3,
            recipeName = "LIGHT ROAST BLOOM",
            ratio = 18.0f,
            dose = 20.0f,
            verdict = Verdict.SOUR
        )
    )
}
