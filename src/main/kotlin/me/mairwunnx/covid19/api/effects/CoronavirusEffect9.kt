package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect9 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect8.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect8.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect8.weaknessDurationTicks * playerInfectedEffectModifier
    const val blindnessDurationTicks =
        CoronavirusEffect8.blindnessDurationTicks * playerInfectedEffectModifier
    const val witherDurationTicks =
        CoronavirusEffect8.witherDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WEAKNESS, (weaknessDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.BLINDNESS, (blindnessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WITHER, (witherDurationTicks).roundToInt(), 1)
    )

    override fun apply(target: ServerPlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
