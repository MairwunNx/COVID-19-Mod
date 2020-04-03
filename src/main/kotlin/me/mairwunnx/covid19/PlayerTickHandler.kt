package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.CoronavirusInfectStatus
import me.mairwunnx.covid19.api.updatePlayerVirusState
import me.mairwunnx.covid19.api.withChance
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PlayerTickHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val name = event.player.name.string

        if (CoronavirusAPI.getMetaIsKilling(name)) return

        val params = CoronavirusAPI.getCoronavirusParameters(
            event.player.world.difficulty.id
        )

        // todo: create minimal time cooldown for chance checking. (e.g 1 - 2min) and decrement time by difficulty.
        withChance(params.playerVirusEffectChanceParam) {
            CoronavirusAPI.getCoronavirusEffectByPercent(
                CoronavirusAPI.getInfectPercent(name)
            ).apply(event.player)
        }

        updatePlayerVirusState(event.player.name.string)
        if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Recession) {
            CoronavirusAPI.disinfectPlayer(name, params.genericDisinfectDosePerTickParam)
        }
        if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Actively) {
            CoronavirusAPI.infectPlayer(name, params.genericInfectDosePerTickParam)
        }
    }
}
