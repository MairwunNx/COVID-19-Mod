package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

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

    override fun getEffects() = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 3),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WEAKNESS, weaknessDurationTicks.toInt(), 3),
        EffectInstance(Effects.BLINDNESS, blindnessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WITHER, witherDurationTicks.toInt(), 1)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
