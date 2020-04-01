package me.mairwunnx.covid19.api

import me.mairwunnx.covid19.api.effects.*
import me.mairwunnx.covid19.api.presets.*
import me.mairwunnx.covid19.api.store.CoronavirusModel
import me.mairwunnx.covid19.api.store.CoronavirusStore
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

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
        Effects.UNLUCK, CoronavirusMediumPreset.infectedMobInfectDurationParam.toInt(), 1
    )

    fun markEntityAsInfected(entity: LivingEntity) {
        val params = getCoronavirusParameters(entity.world.difficulty.id)

        if (cachedEffectInstance.duration != params.infectedMobInfectDurationParam.toInt()) {
            cachedEffectInstance = EffectInstance(
                Effects.UNLUCK,
                params.infectedMobInfectDurationParam.toInt(),
                1
            )
        }
        entity.addPotionEffect(cachedEffectInstance)
    }

    fun isPlayerInfected(name: String): Boolean {
        getPlayerData(name)?.let {
            return it.infectStatus != CoronavirusInfectStatus.None
        }
        return false
    }

    fun isEntityInfected(entity: LivingEntity): Boolean = when (entity) {
        is BatEntity -> true
        else -> entity.activePotionEffects.map {
            it.effectName
        }.contains(Effects.UNLUCK.name)
    }

    fun getInfectInitiatorType(
        entity: LivingEntity
    ): CoronavirusInfectInitiator = when (entity) {
        is ServerPlayerEntity -> CoronavirusInfectInitiator.Player
        else -> CoronavirusInfectInitiator.Entity
    }

    fun getPlayerInfectInitiator(name: String): CoronavirusInfectInitiator? =
        getPlayerData(name)?.let { return it.infectInitiator }

    fun infectPlayer(name: String, dose: Double) {
        getPlayerData(name)?.let {
            it.infectPercent += dose
            it.stage = (it.infectPercent * 0.1).toInt() + 1
        }
    }

    fun infectPlayerInitially(
        name: String,
        initiator: CoronavirusInfectInitiator
    ) {
        getPlayerData(name).let {
            if (it == null) {
                addPlayerData(
                    CoronavirusModel.Player(
                        false, initiator, 0.0, CoronavirusInfectStatus.Actively, false, name, 1
                    )
                )
            } else {
                it.infectStatus = CoronavirusInfectStatus.Actively
                it.infectInitiator = initiator
            }
        }
    }

    fun addPlayerData(model: CoronavirusModel.Player) {
        if (!isPlayerExist(model.player)) CoronavirusStore.take().players.add(model)
    }

    fun getPlayerData(name: String): CoronavirusModel.Player? =
        CoronavirusStore.take().players.find { it.player == name }

    fun isPlayerExist(name: String): Boolean {
        getPlayerData(name)?.let { return true } ?: return false
    }

    fun isPlayerLoggedIn(name: String) = getLoggedInPlayers().contains(name)
    fun setPlayerLoggedIn(name: String) = getLoggedInPlayers().add(name)
    fun getLoggedInPlayers() = CoronavirusStore.take().loggedInPlayers
    fun getCoronavirus() = CoronavirusStore.take().coronavirus
    fun getPlayers() = CoronavirusStore.take().players
}
