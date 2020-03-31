package me.mairwunnx.covid19.api.presets

import me.mairwunnx.covid19.api.*

object CoronavirusMediumPreset : ICoronavirusParameters {
    private val prevPreset = CoronavirusEasyPreset

    override val infectedMobSpawnChanceParam =
        prevPreset.infectedMobSpawnChanceParam * infectedMobSpawnChanceModifier
    override val infectedMobInfectDurationParam =
        prevPreset.infectedMobInfectDurationParam * infectedMobInfectDurationModifier
    override val infectedMobInfectRangeParam =
        prevPreset.infectedMobInfectRangeParam * infectedMobInfectRangeModifier
    override val infectedMobInfectEntityChanceParam =
        prevPreset.infectedMobInfectEntityChanceParam * infectedMobInfectEntityModifier

    override val infectedEatChanceParam =
        prevPreset.infectedEatChanceParam * infectedEatChanceModifier

    override val epidemicChanceParam = prevPreset.epidemicChanceParam
    override val epidemicInfectedMobSpawnChanceParam =
        prevPreset.epidemicInfectedMobSpawnChanceParam * epidemicInfectedMobSpawnChanceModifier
}
