package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.*
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object WorldTickHandler {
    private val epidemicStartMessage = TranslationTextComponent(
        "covid19.epidemic.start_notification"
    )
    private val epidemicStopMessage = TranslationTextComponent(
        "covid19.epidemic.stop_notification"
    )
    private val playerAttacked = TranslationTextComponent(
        "covid19.infect.damage_notify"
    )
    private val playerLivedTakeAChance = TranslationTextComponent(
        "covid19.infect.take_a_chance"
    )
    private val covidDamageSource = DamageSource(
        "covid19"
    ).setDamageBypassesArmor().setDamageAllowedInCreativeMode()
    private var duration: Long = 0

    @SubscribeEvent
    @Suppress("unused")
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.world.dimension.isSurfaceWorld) {
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
                        playSound(it, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.AMBIENT)
                    }
                    event.world.server?.playerList?.sendMessage(epidemicStartMessage)
                }
            } else {
                CoronavirusAPI.getCoronavirus().lastEpidemicTime += 1
                if (CoronavirusAPI.getCoronavirusLastEpidemicTime() >= duration) {
                    CoronavirusAPI.getCoronavirus().epidemic = false
                    CoronavirusAPI.getCoronavirus().epidemics++
                    event.world.server?.playerList?.sendMessage(epidemicStopMessage)
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
                    it.sendMessage(playerLivedTakeAChance)
                } else {
                    it.attackEntityFrom(covidDamageSource, playerDyingDamagePerSecond)
                    CoronavirusAPI.addPlayerKillingTick(name)
                    it.sendMessage(playerAttacked)
                }
            }
        }
    }
}
