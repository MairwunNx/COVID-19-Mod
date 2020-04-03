package me.mairwunnx.covid19.api.effects

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect1 : ICoronavirusEffect {
    const val nauseaDurationTicks = 80
    const val slownessDurationTicks = 20

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks, 1),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks, 1)
    )

    override fun apply(target: PlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
