package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect6 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect5.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect5.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect5.weaknessDurationTicks * playerInfectedEffectModifier

    override fun getEffects() = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 3),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WEAKNESS, weaknessDurationTicks.toInt(), 3)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
