package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.*
import me.mairwunnx.covid19.api.*
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object WorldTickHandler {
    private val covidDamageSource = DamageSource(
        "covid19"
    ).setDamageBypassesArmor().setDamageAllowedInCreativeMode()
    private var duration: Long = 0

    @SubscribeEvent
    @Suppress("unused")
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.world.dimension.isSurfaceWorld) {
            if (!CoronavirusAPI.isCoronavirusFinalized()) {
                if (!CoronavirusAPI.isCoronavirusEpidemicNow()) {
                    withChance(
                        CoronavirusAPI.getCoronavirusParameters(
                            event.world.difficulty.id
                        ).epidemicChanceParam
                    ) {
                        duration = rndLongDecrementOrIncrement(
                            epidemicDurationTicks, epidemicDurationTicksModifier
                        )
                        CoronavirusAPI.getCoronavirus().epidemic = true
                        CoronavirusAPI.getCoronavirus().epidemics++
                        event.world.players.forEach {
                            playSound(it, epidemicStartSound, SoundCategory.AMBIENT)
                        }
                        event.world.players.forEach {
                            it.attackEntityFrom(DamageSource.MAGIC, 2.0f)
                        }
                        event.world.server?.playerList?.sendMessage(epidemicStartMessage)
                    }
                } else {
                    CoronavirusAPI.getCoronavirus().lastEpidemicTime += 1
                    if (CoronavirusAPI.getCoronavirusLastEpidemicTime() >= duration) {
                        CoronavirusAPI.getCoronavirus().epidemic = false
                        CoronavirusAPI.getCoronavirus().lastEpidemicTime = 0
                        event.world.server?.playerList?.sendMessage(epidemicStopMessage)
                        event.world.players.forEach {
                            playSound(it, epidemicStopSound, SoundCategory.AMBIENT)
                        }
                    }
                }
            }

            killInfectedPlayers(event)
        }
    }

    private var passedTicks = 0
    private fun killInfectedPlayers(event: TickEvent.WorldTickEvent) {
        passedTicks += 1
        if (passedTicks == playerKillingDamageEveryTicks) {
            passedTicks = 0

            val candidates = CoronavirusAPI.getPlayers()
                .filter { it.meta.killing && !it.isDead }.map { it.player }

            if (candidates.isEmpty()) return

            event.world.players.filter {
                candidates.contains(it.name.string)
            }.forEach {
                val name = it.name.string
                if (CoronavirusAPI.getMetaKillingTicks(name) >= pardonKillingTicks) {
                    val infectPercent = rndDoubleDecrementOrIncrement(
                        pardonInfectPercent, pardonInfectPercentModifier
                    )
                    CoronavirusAPI.setInfectPercent(name, infectPercent)
                    CoronavirusAPI.refreshPlayerKillingTick(name)
                    updatePlayerVirusState(it.name.string)
                    it.sendMessage(playerLivedTakeAChanceMessage)
                    playSound(it, killingLivedSound, SoundCategory.AMBIENT)
                } else {
                    it.attackEntityFrom(covidDamageSource, playerDyingDamagePerSecond)
                    CoronavirusAPI.addPlayerKillingTick(name)
                }
            }
        }
    }
}
