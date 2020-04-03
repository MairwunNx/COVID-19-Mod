package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect5 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect4.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect4.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect4.weaknessDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WEAKNESS, (weaknessDurationTicks).roundToInt(), 2)
    )

    override fun apply(target: PlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
