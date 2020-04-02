package me.mairwunnx.covid19.api.presets

import me.mairwunnx.covid19.api.*

object CoronavirusMediumPreset : ICoronavirusParameters {
    private val prevPreset = CoronavirusEasyPreset

    override val genericInfectDosePerTickParam =
        prevPreset.genericInfectDosePerTickParam * genericInfectDosePerTickModifier

    override val infectedMobInfectDosePerTickParam =
        prevPreset.infectedMobInfectDosePerTickParam * infectedMobInfectDosePerTickModifier
    override val infectedMobSpawnChanceParam =
        prevPreset.infectedMobSpawnChanceParam * infectedMobSpawnChanceModifier
    override val infectedMobInfectDurationParam =
        prevPreset.infectedMobInfectDurationParam * infectedMobInfectDurationModifier
    override val infectedMobInfectRangeParam =
        prevPreset.infectedMobInfectRangeParam * infectedMobInfectRangeModifier
    override val infectedMobInfectEntityChanceParam =
        prevPreset.infectedMobInfectEntityChanceParam * infectedMobInfectEntityModifier
    override val infectedMobInitiallyInfectEntityChanceParam =
        prevPreset.infectedMobInitiallyInfectEntityChanceParam * infectedMobInitiallyInfectEntityChanceModifier

    override val infectedEatChanceParam =
        prevPreset.infectedEatChanceParam * infectedEatChanceModifier
    override val infectedEatInfectDoseParam =
        prevPreset.infectedEatInfectDoseParam * infectedEatInfectDoseModifier

    override val healingGoldenAppleHealDoseParam =
        prevPreset.healingGoldenAppleHealDoseParam * healingGoldenAppleHealDoseModifier
    override val healingEnchantedGoldenAppleHealDoseParam =
        prevPreset.healingEnchantedGoldenAppleHealDoseParam * healingEnchantedGoldenAppleHealDoseModifier
    override val healingGoldenCarrotHealDoseParam =
        prevPreset.healingGoldenCarrotHealDoseParam * healingGoldenCarrotHealDoseModifier
    override val healingPotionHealDoseParam =
        prevPreset.healingPotionHealDoseParam * healingPotionHealDoseModifier
    override val healingStrongPotionHealDoseParam =
        prevPreset.healingStrongPotionHealDoseParam * healingStrongPotionHealDoseModifier

    override val epidemicChanceParam = prevPreset.epidemicChanceParam
    override val epidemicInfectedMobSpawnChanceParam =
        prevPreset.epidemicInfectedMobSpawnChanceParam * epidemicInfectedMobSpawnChanceModifier

    override val playerVirusEffectChanceParam =
        prevPreset.playerVirusEffectChanceParam * playerVirusEffectChanceModifier
}
