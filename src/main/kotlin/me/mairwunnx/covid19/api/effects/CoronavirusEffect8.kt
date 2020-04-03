package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import kotlin.math.roundToInt

object CoronavirusEffect8 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect7.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect7.slownessDurationTicks * playerInfectedEffectModifier
    const val weaknessDurationTicks =
        CoronavirusEffect7.weaknessDurationTicks * playerInfectedEffectModifier
    const val blindnessDurationTicks =
        CoronavirusEffect7.blindnessDurationTicks * playerInfectedEffectModifier
    const val witherDurationTicks =
        CoronavirusEffect7.blindnessDurationTicks * playerInfectedEffectModifier

    override val effects: ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, (nauseaDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.SLOWNESS, (slownessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WEAKNESS, (weaknessDurationTicks).roundToInt(), 3),
        EffectInstance(Effects.BLINDNESS, (blindnessDurationTicks).roundToInt(), 2),
        EffectInstance(Effects.WITHER, (witherDurationTicks).roundToInt(), 1)
    )

    override fun apply(target: PlayerEntity) {
        effects.forEach { target.addPotionEffect(it) }
    }
}
