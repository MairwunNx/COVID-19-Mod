package me.mairwunnx.covid19.api.presets

interface ICoronavirusParameters {
    val infectedMobSpawnChanceParam: Double
    val infectedMobInfectDurationParam: Double
    val infectedMobInfectRangeParam: Double
    val infectedMobInfectEntityChanceParam: Double
    val infectedEatChanceParam: Double
    val epidemicChanceParam: Double
    val epidemicInfectedMobSpawnChanceParam: Double
}
