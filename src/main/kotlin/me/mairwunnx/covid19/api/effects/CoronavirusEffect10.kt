package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect10 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect9.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect9.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect9.weaknessDurationTicks * playerInfectedEffectModifier
    const val blindnessDurationTicks =
        CoronavirusEffect9.blindnessDurationTicks * playerInfectedEffectModifier
    const val witherDurationTicks =
        CoronavirusEffect9.witherDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WEAKNESS, (weaknessDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.BLINDNESS, (blindnessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WITHER, (witherDurationTicks).roundToInt(), 2)
    )

    override fun apply(target: ServerPlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
