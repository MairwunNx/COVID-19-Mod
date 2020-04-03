package me.mairwunnx.covid19.api.effects

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.potion.EffectInstance

interface ICoronavirusEffect {
    val effects: ArrayList<EffectInstance>
    fun apply(target: PlayerEntity)
}
