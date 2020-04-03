package me.mairwunnx.covid19.api

import me.mairwunnx.covid19.api.presets.CoronavirusMediumPreset
import me.mairwunnx.covid19.api.presets.ICoronavirusParameters
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

internal object CoronavirusCache {
    var cachedEffectInstance = EffectInstance(
        Effects.UNLUCK, CoronavirusMediumPreset.infectedMobInfectDurationParam.toInt(), 1
    )
    var epidemicEffectInstance = EffectInstance(
        Effects.WITHER, CoronavirusMediumPreset.infectedMobInfectDurationParam.toInt(), 1
    )
    var cachedParameters: ICoronavirusParameters = CoronavirusMediumPreset
    var cachedWorldDifficulty = 2
}
