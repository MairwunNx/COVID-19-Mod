package me.mairwunnx.covid19.api.presets

interface ICoronavirusParameters {
    val genericInfectDosePerTickParam: Double
    val infectedMobInfectDosePerTickParam: Double
    val infectedMobSpawnChanceParam: Double
    val infectedMobInfectDurationParam: Double
    val infectedMobInfectRangeParam: Double
    val infectedMobInfectEntityChanceParam: Double
    val infectedMobInitiallyInfectEntityChanceParam: Double
    val infectedEatChanceParam: Double
    val infectedEatInfectDoseParam: Double
    val epidemicChanceParam: Double
    val epidemicInfectedMobSpawnChanceParam: Double
}
