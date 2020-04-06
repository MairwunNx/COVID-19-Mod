package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.*
import me.mairwunnx.covid19.api.*
import me.mairwunnx.covid19.api.CoronavirusAPI.lessOrEquals
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import kotlin.math.roundToInt

object PlayerTickHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val name = event.player.name.string

        if (CoronavirusAPI.getMetaIsKilling(name)) return

        val params = CoronavirusAPI.getCoronavirusParameters(
            event.player.world.difficulty.id
        )

        // todo: create minimal time cooldown for chance checking.
        //  (e.g 1 - 2min) and decrement time by difficulty.
        if (CoronavirusAPI.getInfectPercent(name) > 1) {
            withChance(params.playerVirusEffectChanceParam) {
                event.player.clearActivePotions()
                CoronavirusAPI.getCoronavirusEffectByPercent(
                    CoronavirusAPI.getInfectPercent(name)
                ).apply(event.player)
                event.player.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond * 1.8f)
                playSound(event.player, infectEffectSound, SoundCategory.AMBIENT)
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

        if (CoronavirusAPI.isDisinfectedRewarded(name)) {
            CoronavirusAPI.getPlayerMeta(name)?.disinfectedReward = false
            event.player.addPotionEffect(EffectInstance(Effects.LUCK, 12000, 2))
            val difficulty = event.player.world.difficulty.id
            if (difficulty >= 2) {
                event.player.addPotionEffect(EffectInstance(Effects.SATURATION, 6000, 2))
            }
            if (difficulty == 3) {
                event.player.addPotionEffect(EffectInstance(Effects.STRENGTH, 6000, 2))
            }
            playSound(event.player, healedSound1, SoundCategory.AMBIENT)
            playSound(event.player, healedSound2, SoundCategory.AMBIENT)
            event.player.sendMessage(disinfectedMessage)
            event.player.addExperienceLevel(
                (CoronavirusAPI.getInfectMaxPercent(name) * playerDisinfectedExperienceRewardModifier).roundToInt()
            )
        } else {
            if (CoronavirusAPI.getMetaIsInitiallyInfected(name)) {
                if (CoronavirusAPI.getInfectPercent(name) lessOrEquals 0.0) {
                    playSound(event.player, healedSound2, SoundCategory.AMBIENT)
                    event.player.sendMessage(disinfectedButSimpleMessage)
                }
            }
        }
    }
}
