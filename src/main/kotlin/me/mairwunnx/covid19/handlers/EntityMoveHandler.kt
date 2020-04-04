package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.expandAxisAlignedBB
import me.mairwunnx.covid19.api.playerDyingDamagePerSecond
import me.mairwunnx.covid19.api.withChance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.DamageSource
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object EntityMoveHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onInfectedEntityMove(event: LivingEvent.LivingUpdateEvent) {
        when {
            CoronavirusAPI.isEntityInfected(
                event.entityLiving, CoronavirusAPI.isCoronavirusEpidemicNow()
            ) -> {
                val params = CoronavirusAPI.getCoronavirusParameters(
                    event.entity.world.difficulty.id
                )

                val players = event.entityLiving.world.getEntitiesWithinAABB(
                    PlayerEntity::class.java,
                    expandAxisAlignedBB(
                        params.infectedMobInfectRangeParam, event.entityLiving.boundingBox
                    )
                )

                players.forEach {
                    if (CoronavirusAPI.getMetaIsInitiallyInfected(it.name.string)) {
                        withChance(params.infectedMobInfectEntityChanceParam) {
                            CoronavirusAPI.infectPlayer(
                                it.name.string, params.infectedMobInfectDosePerTickParam
                            )
                        }
                    } else {
                        withChance(params.infectedMobInitiallyInfectEntityChanceParam) {
                            CoronavirusAPI.getCoronavirus().infected++
                            CoronavirusAPI.infectPlayerInitially(
                                it.name.string,
                                CoronavirusAPI.processInfectInitiator(event.entityLiving)
                            )
                            it.attackEntityFrom(DamageSource.MAGIC, playerDyingDamagePerSecond)
                        }
                    }
                }
            }
        }
    }
}
