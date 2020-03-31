/*
    Suppressed some warnings because it redundant in this class.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.expandAxisAlignedBB
import me.mairwunnx.covid19.api.withChance
import me.mairwunnx.covid19.eventbridge.EventBridge
import me.mairwunnx.covid19.eventbridge.ForgeEventType
import net.minecraft.block.Block
import net.minecraft.entity.merchant.villager.VillagerEntity
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.passive.CowEntity
import net.minecraft.entity.passive.PigEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager

@Mod(value = "covid19")
internal class EntryPoint {
    private val logger = LogManager.getLogger()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        EventBridge.initialize()

        EventBridge.addListener(
            this.javaClass, ForgeEventType.DoClientStuff
        ) { doClientStuff(it as FMLClientSetupEvent) }
    }

    internal fun doClientStuff(event: FMLClientSetupEvent) {
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {

    }

    @SubscribeEvent
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

            withChance(params.infectedMobSpawnChanceParam) {
                CoronavirusAPI.markEntityAsInfected(event.entityLiving)
            }
        }
    }

    private var infectedEntityTicks = 0

    @SubscribeEvent
    fun onInfectedEntityMove(event: LivingEvent.LivingUpdateEvent) {
        infectedEntityTicks += 1

        if (infectedEntityTicks == 5) {
            infectedEntityTicks = 0

            if (CoronavirusAPI.isEntityInfected(event.entityLiving)) {
                val params = CoronavirusAPI.getCoronavirusParameters(
                    event.entity.world.difficulty.id
                )

                val players = event.entityLiving.world.getEntitiesWithinAABB(
                    ServerPlayerEntity::class.java,
                    expandAxisAlignedBB(
                        params.infectedMobInfectRangeParam,
                        event.entityLiving.boundingBox
                    )
                )

                players.forEach {
                    // if (CoronavirusAPI.isPlayerInfected(player)) {
//                      withChance(params.infectedMobInfectEntityChanceParam) {
//                          CoronavirusAPI.infectPlayer(player, params.infectDosePerTick)
//                      }
//                  } else {
//                      withChance(params.infectedMobInitiallyInfectEntityChanceParam) {
//                          CoronavirusAPI.infectPlayerInitially(player, getInitiatorType(event.entityLiving))
//                      }
//                  }
                }
            }
        }
    }

    @SubscribeEvent
    fun onMeatEating(event: LivingEntityUseItemEvent.Start) {
        if (
            event.item.item == Items.BEEF ||
            event.item.item == Items.CHICKEN ||
            event.item.item == Items.SPIDER_EYE ||
            event.item.item == Items.RABBIT ||
            event.item.item == Items.MUTTON ||
            event.item.item == Items.SALMON ||
            event.item.item == Items.COD
        ) {
            val params = CoronavirusAPI.getCoronavirusParameters(
                event.entity.world.difficulty.id
            )

            // if (CoronavirusAPI.isPlayerInfected(player)) {
//                      withChance(params.infectedMobInfectEntityChanceParam) {
//                          CoronavirusAPI.infectPlayer(player, params.infectDosePerTick)
//                      }
//                  } else {
//                      withChance(params.infectedMobInitiallyInfectEntityChanceParam) {
//                          CoronavirusAPI.infectPlayerInitially(player, Eat)
//                      }
//                  }
        }
    }

    object RegistryEvents {
        fun onBlocksRegistry(event: Register<Block>) {

        }

        fun onItemsRegistry(event: Register<Item>) {

        }
    }
}
