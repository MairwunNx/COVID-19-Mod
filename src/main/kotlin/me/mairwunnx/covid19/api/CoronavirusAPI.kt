package me.mairwunnx.covid19.api

import me.mairwunnx.covid19.api.effects.*
import me.mairwunnx.covid19.api.presets.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.math.AxisAlignedBB
import kotlin.math.roundToInt

object CoronavirusAPI {
    private var cachedWorldDifficulty = 2
    private var cachedParameters: ICoronavirusParameters = CoronavirusMediumPreset

    fun getCoronavirusParameters(
        difficulty: Int
    ): ICoronavirusParameters {
        if (difficulty == cachedWorldDifficulty) {
            return cachedParameters
        } else {
            cachedWorldDifficulty = difficulty
            cachedParameters = when (difficulty) {
                0 -> CoronavirusPeacefulPreset
                1 -> CoronavirusEasyPreset
                2 -> CoronavirusMediumPreset
                3 -> CoronavirusHardPreset
                else -> CoronavirusEasyPreset
            }
        }
        return cachedParameters
    }

    fun getCoronavirusEffectByPercent(
        percent: Double
    ): ICoronavirusEffect = when (percent) {
        in 0.0..10.0 -> CoronavirusEffect1
        in 10.0..20.0 -> CoronavirusEffect2
        in 20.0..30.0 -> CoronavirusEffect3
        in 30.0..40.0 -> CoronavirusEffect4
        in 40.0..50.0 -> CoronavirusEffect5
        in 50.0..60.0 -> CoronavirusEffect6
        in 60.0..70.0 -> CoronavirusEffect7
        in 70.0..80.0 -> CoronavirusEffect8
        in 80.0..90.0 -> CoronavirusEffect9
        in 90.0..100.0 -> CoronavirusEffect10
        else -> CoronavirusEffect1
    }

    private var cachedEffectInstance = EffectInstance(
        Effects.UNLUCK, CoronavirusMediumPreset.infectedMobInfectDurationParam.roundToInt(), 1
    )

    fun markEntityAsInfected(entity: LivingEntity) {
        val params = getCoronavirusParameters(entity.world.difficulty.id)

        if (cachedEffectInstance.duration != params.infectedMobInfectDurationParam.roundToInt()) {
            cachedEffectInstance = EffectInstance(
                Effects.UNLUCK,
                params.infectedMobInfectDurationParam.roundToInt(),
                1
            )
        }
        entity.addPotionEffect(cachedEffectInstance)
    }

    fun getInfectInitiatorType(
        entity: LivingEntity
    ): CoronavirusInfectInitiator = when (entity) {
        is ServerPlayerEntity -> CoronavirusInfectInitiator.Player
        else -> CoronavirusInfectInitiator.Entity
    }

    fun isEntityInfected(entity: LivingEntity): Boolean {
        return if (entity is BatEntity) {
            true
        } else {
            entity.activePotionEffects.map {
                it.effectName
            }.contains(Effects.UNLUCK.name)
        }
    }
}
