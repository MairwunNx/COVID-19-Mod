package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect3 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect2.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect2.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect2.nauseaDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 1),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 1),
        EffectInstance(Effects.WEAKNESS, (weaknessDurationTicks).roundToInt(), 1)
    )

    override fun apply(target: ServerPlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
