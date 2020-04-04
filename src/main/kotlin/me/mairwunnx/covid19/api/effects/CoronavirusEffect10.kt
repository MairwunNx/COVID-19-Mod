package me.mairwunnx.covid19.api.effects

import me.mairwunnx.covid19.api.playerInfectedEffectModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects

object CoronavirusEffect10 : ICoronavirusEffect {
    const val nauseaDurationTicks =
        CoronavirusEffect9.nauseaDurationTicks * playerInfectedEffectModifier
    const val slownessDurationTicks =
        CoronavirusEffect9.slownessDurationTicks * playerInfectedEffectModifier
    private const val weaknessDurationTicks =
        CoronavirusEffect9.weaknessDurationTicks * playerInfectedEffectModifier
    private const val blindnessDurationTicks =
        CoronavirusEffect9.blindnessDurationTicks * playerInfectedEffectModifier
    private const val witherDurationTicks =
        CoronavirusEffect9.witherDurationTicks * playerInfectedEffectModifier

    override fun getEffects(): ArrayList<EffectInstance> = arrayListOf(
        EffectInstance(Effects.NAUSEA, nauseaDurationTicks.toInt(), 3),
        EffectInstance(Effects.SLOWNESS, slownessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WEAKNESS, weaknessDurationTicks.toInt(), 3),
        EffectInstance(Effects.BLINDNESS, blindnessDurationTicks.toInt(), 2),
        EffectInstance(Effects.WITHER, witherDurationTicks.toInt(), 2)
    )

    override fun apply(target: PlayerEntity) {
        getEffects().forEach { target.addPotionEffect(it) }
    }
}
