package me.mairwunnx.covid19.api.presets

import me.mairwunnx.covid19.api.*

object CoronavirusPeacefulPreset : ICoronavirusParameters {
    override val infectedMobSpawnChanceParam = infectedMobSpawnChance
    override val infectedMobInfectDurationParam = infectedMobInfectDuration
    override val infectedMobInfectRangeParam = infectedMobInfectRange
    override val infectedMobInfectEntityChanceParam = infectedMobInfectEntityChance

    override val infectedEatChanceParam = infectedEatChance

    override val epidemicChanceParam = epidemicChance
    override val epidemicInfectedMobSpawnChanceParam = epidemicInfectedMobSpawnChance
}
