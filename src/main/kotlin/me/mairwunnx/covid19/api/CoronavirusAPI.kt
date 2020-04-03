@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package me.mairwunnx.covid19.api

import me.mairwunnx.covid19.api.CoronavirusCache.cachedEffectInstance
import me.mairwunnx.covid19.api.CoronavirusCache.cachedParameters
import me.mairwunnx.covid19.api.CoronavirusCache.cachedWorldDifficulty
import me.mairwunnx.covid19.api.CoronavirusCache.epidemicEffectInstance
import me.mairwunnx.covid19.api.effects.*
import me.mairwunnx.covid19.api.presets.*
import me.mairwunnx.covid19.api.store.CoronavirusModel
import me.mairwunnx.covid19.api.store.CoronavirusStore
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.BatEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.abs

/**
 * Coronavirus internal API. Currently not able
 * for interacting with external modules, because
 * library not distributing on maven repositories.
 */
object CoronavirusAPI {
    /**
     * Marks entity with infected effect (unluck effect).
     * @param entity entity to mark infected.
     */
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

    /**
     * Marks entity with epidemic effect (wither effect).
     * @param entity entity to mark epidemic.
     */
    fun markEntityAsEpidemic(entity: LivingEntity) {
        val params = getCoronavirusParameters(entity.world.difficulty.id)
        if (epidemicEffectInstance.duration != params.infectedMobInfectDurationParam.toInt()) {
            epidemicEffectInstance = EffectInstance(
                Effects.WITHER,
                params.infectedMobInfectDurationParam.toInt(),
                1
            )
        }
        entity.addPotionEffect(epidemicEffectInstance)
    }

    /**
     * @param difficulty world difficulty.
     * @return coronavirus parameters.
     */
    fun getCoronavirusParameters(difficulty: Int): ICoronavirusParameters {
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

    /**
     * @param percent player infect percent.
     * @return coronavirus effect related for specified
     * infect percent.
     */
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

    /**
     * @return coronavirus data model.
     */
    fun getCoronavirus() = CoronavirusStore.take().coronavirus

    /**
     * @return coronavirus deaths count.
     */
    fun getCoronavirusDeaths() = CoronavirusStore.take().coronavirus.deaths

    /**
     * @return true if now is epidemic.
     */
    fun isCoronavirusEpidemicNow() = CoronavirusStore.take().coronavirus.epidemic

    /**
     * @return ticks count as last epidemic duration.
     */
    fun getCoronavirusLastEpidemicTime() = CoronavirusStore.take().coronavirus.lastEpidemicTime

    /**
     * @return coronavirus epidemics count.
     */
    fun getCoronavirusEpidemics() = CoronavirusStore.take().coronavirus.epidemics

    /**
     * @return coronavirus infected entities by epidemic count.
     */
    fun getCoronavirusInfectedByEpidemic() = CoronavirusStore.take().coronavirus.epidemicInfected

    /**
     * @return true if coronavirus finalized otherwise false.
     */
    fun isCoronavirusFinalized() = CoronavirusStore.take().coronavirus.finalized

    /**
     * @return general coronavirus infected entities count.
     * Includes [getCoronavirusInfectedByEpidemic] method result.
     */
    fun getCoronavirusInfected() = CoronavirusStore.take().coronavirus.infected

    /**
     * @return all infected players.
     */
    fun getPlayers() = CoronavirusStore.take().players

    /**
     * @param name player nickname.
     * @return player data class instance by nickname. Return
     * null if player not exist.
     */
    fun getPlayer(name: String) = CoronavirusStore.take().players.find {
        it.player == name
    }

    /**
     * @param name player nickname.
     * @return player infect percent by player name. Return
     * `0.0` value if player not exist.
     */
    fun getInfectPercent(name: String) = getPlayer(name)?.infectPercent ?: 0.0

    /**
     * @param name player nickname.
     * @return true if player disinfected (has 0.0
     * infect percents).
     */
    fun isPlayerDisinfected(name: String): Boolean =
        getPlayer(name)?.infectPercent?.lessOrEquals(disinfectedInfectPercent) ?: true

    /**
     * @param name player nickname.
     * @return player infect status enum element. Return
     * [CoronavirusInfectStatus.None] if player not exist.
     */
    fun getInfectStatus(name: String) =
        getPlayer(name)?.infectStatus ?: CoronavirusInfectStatus.None

    /**
     * @param name player nickname.
     * @return player infect initiator enum element. Return
     * [CoronavirusInfectInitiator.None] if player not exist.
     */
    fun getInfectInitiator(name: String) =
        getPlayer(name)?.infectInitiator ?: CoronavirusInfectInitiator.None

    /**
     * @param name player nickname.
     * @return true if player has immunity to coronavirus.
     */
    fun isPlayerHasImmunity(name: String) = getPlayer(name)?.hasImmunity ?: false

    /**
     * @param name player nickname.
     * @return player infect stage.
     */
    fun getPlayerInfectStage(name: String) = getPlayer(name)?.infectStage ?: 0

    /**
     * @param name player nickname.
     * @return true if player dead by coronavirus.
     */
    fun isPlayerDead(name: String) = getPlayer(name)?.isDead ?: false

    /**
     * @param name player nickname.
     * @return player meta data class. Return null if player not exist.
     */
    fun getPlayerMeta(name: String) = getPlayer(name)?.meta

    /**
     * @param name player nickname.
     * @return return true if player logged-in in world.
     */
    fun getMetaIsLoggedIn(name: String) = getPlayer(name)?.meta?.loggedIn ?: false

    /**
     * @param name player nickname.
     * @return return true if player was infected.
     */
    fun getMetaIsInitiallyInfected(name: String) = getPlayer(name)?.meta?.initiallyInfected ?: false

    /**
     * @param name player nickname.
     * @return return true if player was disinfected.
     */
    fun getMetaIsInitiallyDisinfected(name: String) =
        getPlayer(name)?.meta?.initiallyDisinfected ?: false

    /**
     * @param name player nickname.
     * @return return true if player killing by coronavirus.
     */
    fun getMetaIsKilling(name: String) = getPlayer(name)?.meta?.killing ?: false

    /**
     * @param name player nickname.
     * @return return passed ticks while player killed.
     */
    fun getMetaKillingTicks(name: String) = getPlayer(name)?.meta?.killingTicks ?: 0

    /**
     * @param entity target entity to check.
     * @param isEpidemic is epidemic entity?
     * @return true if entity infected or epidemic
     * if [isEpidemic] is true.
     */
    fun isEntityInfected(
        entity: LivingEntity, isEpidemic: Boolean = false
    ): Boolean = when (entity) {
        is BatEntity -> true
        else -> entity.activePotionEffects.map { it.effectName }.contains(
            if (isEpidemic) Effects.WITHER.name else Effects.UNLUCK.name
        )
    }

    /**
     * @param entity target entity to check "who is?".
     * @return [CoronavirusInfectInitiator] enum element.
     * Return [CoronavirusInfectInitiator.Player] if initiator
     * instance of [ServerPlayerEntity], otherwise return
     * [CoronavirusInfectInitiator.Entity].
     */
    fun processInfectInitiator(
        entity: LivingEntity
    ): CoronavirusInfectInitiator = when (entity) {
        is ServerPlayerEntity -> CoronavirusInfectInitiator.Player
        else -> CoronavirusInfectInitiator.Entity
    }

    /**
     * Disinfect target player with specified dose.
     * @param name target player name to disinfect.
     * @param dose disinfect dose.
     */
    fun disinfectPlayer(name: String, dose: Double) {
        getPlayer(name)?.let {
            if (!isPlayerDisinfected(name)) {
                if (!isPlayerDisinfected(name)) {
                    it.meta.initiallyDisinfected = true
                }

                it.infectPercent -= dose
                it.infectStage = (it.infectPercent * 0.1).toInt() + 1

                // Note ↓: Checking status again after decrement infect percent.
                if (isPlayerDisinfected(name)) {
                    it.infectStatus = CoronavirusInfectStatus.None
                    it.hasImmunity = true
                    it.infectPercent = disinfectedInfectPercent
                    it.infectStage = disinfectedInfectStage
                }
            }
        }
    }

    /**
     * Infect target player with specified dose.
     * @param name target player name to infect.
     * @param dose infect dose.
     */
    fun infectPlayer(name: String, dose: Double) {
        getPlayer(name)?.let {
            it.infectPercent += dose
            it.infectStage = (it.infectPercent * 0.1).toInt() + 1

            if (it.infectPercent greatOrEquals infectedInfectPercent) {
                it.infectPercent = infectedInfectPercent
                it.infectStage = infectedInfectStage
                it.meta.killing = true
            }
        }
    }

    /**
     * Force install infect percents to player.
     * @param name target player name.
     * @param percent new percent value.
     */
    fun setInfectPercent(name: String, percent: Double) {
        getPlayer(name)?.let {
            it.infectPercent = percent
            it.infectStage = (it.infectPercent * 0.1).toInt() + 1
            it.meta.killing = it.infectPercent greatOrEquals infectedInfectPercent
        }
    }

    /**
     * Infects player initially. (Adds player data).
     * @param name player name.
     * @param initiator infect initiator.
     */
    fun infectPlayerInitially(
        name: String,
        initiator: CoronavirusInfectInitiator
    ) = getPlayer(name)?.let {
        it.infectStatus = CoronavirusInfectStatus.Actively
        it.infectInitiator = initiator
        it.meta.initiallyInfected = true
    } ?: run {
        addPlayerData(
            CoronavirusModel.Player(
                player = name,
                infectStatus = CoronavirusInfectStatus.Actively,
                infectInitiator = initiator
            ).apply {
                this.meta.initiallyInfected = true
            }
        )
    }

    /**
     * Add new player to [CoronavirusModel.Player] data class.
     * @param model player data model.
     */
    fun addPlayerData(model: CoronavirusModel.Player) {
        if (!isPlayerExist(model.player)) getPlayers().add(model)
    }

    /**
     * @param name player name.
     * @return true if player exits.
     */
    fun isPlayerExist(name: String): Boolean = getPlayer(name) != null

    /**
     * Do refresh player killing ticks.
     * @param name player name.
     */
    fun refreshPlayerKillingTick(name: String) {
        getPlayer(name)?.let { it.meta.killingTicks = initialKillingTicks }
    }

    /**
     * Adds player killing tick with value.
     * @param name player name.
     */
    fun addPlayerKillingTick(name: String) {
        getPlayer(name)?.let { it.meta.killingTicks += 1 }
    }

    /**
     * @return true if double value less or equals.
     */
    infix fun Double.lessOrEquals(other: Double) = this < other || this.equal(other)

    /**
     * @return true if double value greatness or equals.
     */
    infix fun Double.greatOrEquals(other: Double) = this > other || this.equal(other)

    /**
     * @return true if double value equals.
     */
    infix fun Double.equal(other: Double) = abs(this - other) < 0.000001
}
