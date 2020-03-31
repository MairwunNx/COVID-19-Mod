package me.mairwunnx.covid19.api.effects

import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect1 : ICoronavirusEffect {
    const val nauseaDurationTicks = 80
    const val slownessDurationTicks = 20

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, 80, 1),
        EffectInstance(Effects.SLOWNESS, 20, 1)
    )

    override fun apply(target: ServerPlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
