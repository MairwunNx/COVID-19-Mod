package me.mairwunnx.covid19.api.presets

import me.mairwunnx.covid19.api.*

object CoronavirusPeacefulPreset : ICoronavirusParameters {
    override val genericInfectDosePerTickParam = genericInfectDosePerTick

    override val infectedMobInfectDosePerTickParam = infectedMobInfectDosePerTick
    override val infectedMobSpawnChanceParam = infectedMobSpawnChance
    override val infectedMobInfectDurationParam = infectedMobInfectDuration
    override val infectedMobInfectRangeParam = infectedMobInfectRange
    override val infectedMobInfectEntityChanceParam = infectedMobInfectEntityChance
    override val infectedMobInitiallyInfectEntityChanceParam =
        infectedMobInitiallyInfectEntityChance

    override val infectedEatChanceParam = infectedEatChance
    override val infectedEatInfectDoseParam = infectedEatInfectDose

    override val epidemicChanceParam = epidemicChance
    override val epidemicInfectedMobSpawnChanceParam = epidemicInfectedMobSpawnChance
}
