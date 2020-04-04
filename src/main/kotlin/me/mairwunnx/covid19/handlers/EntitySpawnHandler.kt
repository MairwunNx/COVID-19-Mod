package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.withChance
import net.minecraft.entity.merchant.villager.VillagerEntity
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.passive.CowEntity
import net.minecraft.entity.passive.PigEntity
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object EntitySpawnHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onEntitySpawn(event: LivingSpawnEvent) {
        if (
            event.entity is PigEntity ||
            event.entity is CowEntity ||
            event.entity is ChickenEntity ||
            event.entity is ZombieEntity ||
            event.entity is VillagerEntity ||
            event.entity is CreeperEntity ||
            event.entity is ZombiePigmanEntity ||
            event.entity is CaveSpiderEntity ||
            event.entity is EvokerEntity
        ) {
            val params = CoronavirusAPI.getCoronavirusParameters(
                event.world.difficulty.id
            )

            if (CoronavirusAPI.isCoronavirusEpidemicNow()) {
                withChance(params.epidemicInfectedMobSpawnChanceParam) {
                    CoronavirusAPI.getCoronavirus().infected++
                    CoronavirusAPI.getCoronavirus().epidemicInfected++
                    CoronavirusAPI.markEntityAsEpidemic(event.entityLiving)
                }
            } else {
                withChance(params.infectedMobSpawnChanceParam) {
                    CoronavirusAPI.getCoronavirus().infected++
                    CoronavirusAPI.markEntityAsInfected(event.entityLiving)
                }
            }
        }
    }
}
