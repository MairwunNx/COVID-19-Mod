package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.*
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
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
        if (CoronavirusAPI.getInfectPercent(name) > 1) {
            withChance(params.playerVirusEffectChanceParam) {
                CoronavirusAPI.getCoronavirusEffectByPercent(
                    CoronavirusAPI.getInfectPercent(name)
                ).apply(event.player)
                playSound(event.player, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.AMBIENT)
            }
        }

        if (CoronavirusAPI.getMetaIsInitiallyInfected(name)) {
            updatePlayerVirusState(event.player.name.string)
            if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Recession) {
                CoronavirusAPI.disinfectPlayer(name, params.genericDisinfectDosePerTickParam)
            }
            if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Actively) {
                CoronavirusAPI.infectPlayer(name, params.genericInfectDosePerTickParam)
            }
        }
    }
}
