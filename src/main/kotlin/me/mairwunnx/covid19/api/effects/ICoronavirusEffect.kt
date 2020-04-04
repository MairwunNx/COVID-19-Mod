package me.mairwunnx.covid19.api.effects

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.EffectInstance

interface ICoronavirusEffect {
    fun getEffects(): ArrayList<EffectInstance>
    fun apply(target: PlayerEntity)
}
