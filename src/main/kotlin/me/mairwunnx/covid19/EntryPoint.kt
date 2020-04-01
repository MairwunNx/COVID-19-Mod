@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.*
import me.mairwunnx.covid19.api.store.CoronavirusStore
import me.mairwunnx.covid19.eventbridge.EventBridge
import net.minecraft.block.Block
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.entity.merchant.villager.VillagerEntity
import net.minecraft.entity.monster.*
import net.minecraft.entity.passive.ChickenEntity
import net.minecraft.entity.passive.CowEntity
import net.minecraft.entity.passive.PigEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import java.io.File

@Mod(value = "covid19")
internal class EntryPoint {
    private val logger = LogManager.getLogger()

    init {
        EventBridge.initialize()
        MinecraftForge.EVENT_BUS.register(this)
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
                CoronavirusAPI.getCoronavirus().infected++
                CoronavirusAPI.markEntityAsInfected(event.entityLiving)
            }
        }
    }

    private var infectedEntityTicks = 0

    @SubscribeEvent
    fun onInfectedEntityMove(event: LivingEvent.LivingUpdateEvent) {
        infectedEntityTicks += 1
        if (infectedEntityTicks == infectedEntityChecksEveryTicks) {
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
                    if (CoronavirusAPI.isPlayerInfected(it.name.string)) {
                        withChance(params.infectedMobInfectEntityChanceParam) {
                            CoronavirusAPI.infectPlayer(
                                it.name.string,
                                params.infectedMobInfectDosePerTickParam
                            )
                        }
                    } else {
                        withChance(params.infectedMobInitiallyInfectEntityChanceParam) {
                            CoronavirusAPI.getCoronavirus().infected++
                            CoronavirusAPI.infectPlayerInitially(
                                it.name.string,
                                CoronavirusAPI.getInfectInitiatorType(event.entityLiving)
                            )
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onFirstPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.player
        if (!CoronavirusAPI.isPlayerLoggedIn(player.name.string)) {
            CoronavirusAPI.setPlayerLoggedIn(player.name.string)

            repeat(2) { row ->
                for (lightning in 1..4) {
                    val lightningInstance = LightningBoltEntity(
                        player.world,
                        when {
                            !isEvenNumber(lightning) -> getLightningPosByRow(
                                player.posX, row, lightning
                            )
                            else -> player.posX
                        },
                        player.posY,
                        when {
                            isEvenNumber(lightning) -> getLightningPosByRow(
                                player.posZ, row, lightning
                            )
                            else -> player.posZ
                        },
                        true
                    )
                    (player.world as ServerWorld).addLightningBolt(lightningInstance)
                    player.onStruckByLightning(lightningInstance)
                }
            }

            TranslationTextComponent("covid19.welcomeMessage")
            player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.5f, 1.0f)
        }
    }

    private fun getLightningPosByRow(
        pos: Double, row: Int, lightning: Int
    ) = when {
        isEvenNumber(lightning) -> when (lightning) {
            2 -> pos + playerJoinLightningRowDistance * row
            else -> pos - playerJoinLightningRowDistance * row
        }
        else -> when (lightning) {
            1 -> pos + playerJoinLightningRowDistance * row
            else -> pos - playerJoinLightningRowDistance * row
        }
    }

    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        // with chance call epidemic
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        // with chance apply effects on player by stage
        // handle player virus: suspended, actively, recession
    }

    @SubscribeEvent
    fun onMeatEating(event: LivingEntityUseItemEvent.Start) {
        if (event.entityLiving is ServerPlayerEntity) {
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
                val player = event.entityLiving as ServerPlayerEntity

                if (CoronavirusAPI.isPlayerInfected(player.name.string)) {
                    withChance(params.infectedMobInfectEntityChanceParam) {
                        CoronavirusAPI.infectPlayer(
                            player.name.string,
                            params.infectedEatInfectDoseParam
                        )
                    }
                } else {
                    withChance(params.infectedMobInitiallyInfectEntityChanceParam) {
                        CoronavirusAPI.getCoronavirus().infected++
                        CoronavirusAPI.infectPlayerInitially(
                            player.name.string, CoronavirusInfectInitiator.Eat
                        )
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) = initializeCoronavirusStore(event)

    @SubscribeEvent
    fun onServerShuttingDown(event: FMLServerStoppingEvent) = CoronavirusStore.save()

    private fun initializeCoronavirusStore(event: FMLServerStartingEvent) {
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                CoronavirusStore.init("saves${File.separator}${event.server.worldName}")
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                CoronavirusStore.init(event.server.folderName)
            }
        }
    }

    object RegistryEvents {
        fun onBlocksRegistry(event: Register<Block>) {

        }

        fun onItemsRegistry(event: Register<Item>) {

        }
    }
}
