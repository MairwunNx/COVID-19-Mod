package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect7 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect6.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect6.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect6.weaknessDurationTicks * playerInfectedEffectModifier
    const val blindnessDurationTicks =
        CoronavirusEffect6.slownessDurationTicks * playerInfectedEffectModifier

    override fun getEffects() = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 3),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WEAKNESS, weaknessDurationTicks.toInt(), 3),
        EffectInstance(Effects.BLINDNESS, blindnessDurationTicks.toInt(), 2)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
