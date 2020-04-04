package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect2 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect1.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect1.slownessDurationTicks * playerInfectedEffectModifier

    override fun getEffects() = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 1),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 1)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
