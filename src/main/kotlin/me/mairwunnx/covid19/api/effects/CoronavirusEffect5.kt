package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect5 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect4.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect4.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect4.weaknessDurationTicks * playerInfectedEffectModifier

    override fun getEffects() = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 2),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WEAKNESS, weaknessDurationTicks.toInt(), 2)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
