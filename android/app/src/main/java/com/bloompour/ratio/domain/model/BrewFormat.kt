package com.bloompour.ratio.domain.model

import com.bloompour.ratio.core.config.GameConfig
import kotlin.math.abs
import kotlin.math.roundToInt

object BrewFormat {

    fun ratioText(ratio: Float): String {
        val snapped = (ratio * 2f).roundToInt() / 2f
        return if (abs(snapped - snapped.toInt()) < 0.01f) {
            snapped.toInt().toString()
        } else {
            snapped.toInt().toString() + "." + ((abs(snapped - snapped.toInt()) * 10f).roundToInt()).toString()
        }
    }

    fun doseText(dose: Float): String {
        val snapped = (dose * 10f).roundToInt() / 10f
        return if (abs(snapped - snapped.toInt()) < 0.05f) {
            snapped.toInt().toString()
        } else {
            snapped.toInt().toString() + "." + ((abs(snapped - snapped.toInt()) * 10f).roundToInt()).toString()
        }
    }

    fun weightText(grams: Float, useOunces: Boolean): String {
        if (!useOunces) {
            return grams.roundToInt().toString()
        }
        val ounces = grams / GameConfig.GRAMS_PER_OUNCE
        val whole = ounces.toInt()
        val decimal = ((ounces - whole) * 10f).roundToInt()
        return if (decimal >= 10) (whole + 1).toString() else whole.toString() + "." + decimal.toString()
    }

    fun timeText(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val padded = if (seconds < 10) "0" + seconds.toString() else seconds.toString()
        return minutes.toString() + ":" + padded
    }

    fun multiplierText(value: Float): String {
        val snapped = (value * 2f).roundToInt() / 2f
        val whole = snapped.toInt()
        val decimal = ((snapped - whole) * 10f).roundToInt()
        return whole.toString() + "." + decimal.toString()
    }
}
