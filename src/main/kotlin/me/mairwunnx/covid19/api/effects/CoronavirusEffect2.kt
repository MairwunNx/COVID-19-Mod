package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect2 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect1.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect1.slownessDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 1),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 1)
    )

    override fun apply(target: ServerPlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
