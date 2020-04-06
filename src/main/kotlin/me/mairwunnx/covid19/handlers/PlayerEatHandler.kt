package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.*
import me.mairwunnx.covid19.eatHealingSound
import me.mairwunnx.covid19.infectedMessage
import me.mairwunnx.covid19.virusRebornMessage
import me.mairwunnx.covid19.virusRebornSound
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PlayerEatHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerEating(event: LivingEntityUseItemEvent.Finish) {
        if (event.entityLiving is ServerPlayerEntity) {
            if (
                event.item.item == Items.BEEF ||
                event.item.item == Items.CHICKEN ||
                event.item.item == Items.SPIDER_EYE ||
                event.item.item == Items.RABBIT ||
                event.item.item == Items.MUTTON ||
                event.item.item == Items.SALMON ||
                event.item.item == Items.COD
            ) onInfectedMeatEating(event)
            onHealingEatEating(event)
        }
    }

    private fun onInfectedMeatEating(event: LivingEntityUseItemEvent.Finish) {
        val params = CoronavirusAPI.getCoronavirusParameters(
            event.entity.world.difficulty.id
        )
        val player = event.entityLiving as ServerPlayerEntity

        if (CoronavirusAPI.isPlayerHasImmunity(player.name.string)) return

        if (CoronavirusAPI.getMetaIsInitiallyInfected(player.name.string)) {
            if (!CoronavirusAPI.isCoronavirusFinalized()) {
                withChance(params.infectedEatChanceParam) {
                    CoronavirusAPI.infectPlayer(
                        player.name.string, params.infectedEatInfectDoseParam
                    )
                    player.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond * 2.2f)
                }
            } else {
                withChance(params.coronavirusRebornChanceParam) {
                    CoronavirusAPI.infectPlayer(
                        player.name.string, params.infectedEatInfectDoseParam * 2.8
                    )
                    CoronavirusAPI.getCoronavirus().finalized = false
                    player.server.playerList.players.forEach {
                        repeat(2) { _ ->
                            playSound(it, virusRebornSound, SoundCategory.AMBIENT)
                        }
                    }
                    player.server.playerList.sendMessage(virusRebornMessage)
                    player.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond * 2.2f)
                }
            }
        } else {
            if (!CoronavirusAPI.isCoronavirusFinalized()) {
                withChance(params.infectedEatChanceParam) {
                    CoronavirusAPI.getCoronavirus().infected++
                    CoronavirusAPI.infectPlayerInitially(
                        player.name.string, CoronavirusInfectInitiator.Eat
                    )
                    player.sendMessage(infectedMessage)
                    player.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond * 2.2f)
                }
            } else {
                withChance(params.coronavirusRebornChanceParam) {
                    CoronavirusAPI.infectPlayerInitially(
                        player.name.string,
                        CoronavirusInfectInitiator.Eat
                    )
                    CoronavirusAPI.infectPlayer(
                        player.name.string,
                        params.infectedEatInfectDoseParam * 4.4
                    )
                    CoronavirusAPI.getCoronavirus().finalized = false
                    player.server.playerList.players.forEach {
                        repeat(2) { _ ->
                            playSound(it, virusRebornSound, SoundCategory.AMBIENT)
                        }
                    }
                    player.sendMessage(infectedMessage)
                    player.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond * 2.2f)
                    player.server.playerList.sendMessage(virusRebornMessage)
                }
            }
        }
    }

    private fun onHealingEatEating(event: LivingEntityUseItemEvent.Finish) {
        val params = CoronavirusAPI.getCoronavirusParameters(
            event.entity.world.difficulty.id
        )
        val player = event.entityLiving as ServerPlayerEntity
        var healPercentage = 0.0

        when (event.item.item) {
            Items.GOLDEN_APPLE -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingGoldenAppleHealDoseParam,
                healingGoldenAppleHealDoseFloat
            )
            Items.ENCHANTED_GOLDEN_APPLE -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingEnchantedGoldenAppleHealDoseParam,
                healingEnchantedGoldenAppleHealDoseFloat
            )
            Items.GOLDEN_CARROT -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingGoldenCarrotHealDoseParam,
                healingGoldenCarrotHealDoseFloat
            )
            Potions.HEALING -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingPotionHealDoseParam,
                healingPotionHealDoseFloat
            )
            Potions.STRONG_HEALING -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingStrongPotionHealDoseParam,
                healingStrongPotionHealDoseFloat
            )
        }
        if (healPercentage != 0.0) {
            playSound(player, eatHealingSound, SoundCategory.AMBIENT)
            CoronavirusAPI.disinfectPlayer(player.name.string, healPercentage)
        }
    }
}
