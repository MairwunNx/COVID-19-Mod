@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.*
import me.mairwunnx.covid19.api.store.CoronavirusStore
import me.mairwunnx.covid19.eventbridge.EventBridge
import net.minecraft.block.Block
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.server.management.ProfileBanEntry
import net.minecraft.util.DamageSource
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager
import java.io.File
import kotlin.math.abs

@Mod(value = "covid19")
internal class EntryPoint {
    private val logger = LogManager.getLogger()
    private val covidDamageSource = DamageSource(
        "covid19"
    ).setDamageBypassesArmor().setDamageAllowedInCreativeMode()

    init {
        EventBridge.initialize()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(EntitySpawnHandler)
    }

    @SubscribeEvent
    fun onInfectedEntityMove(event: LivingEvent.LivingUpdateEvent) {
        when {
            CoronavirusAPI.isEntityInfected(
                event.entityLiving, CoronavirusTemporaryState.epidemic
            ) -> {
                val params = CoronavirusAPI.getCoronavirusParameters(
                    event.entity.world.difficulty.id
                )

                val players = event.entityLiving.world.getEntitiesWithinAABB(
                    PlayerEntity::class.java,
                    expandAxisAlignedBB(
                        params.infectedMobInfectRangeParam,
                        event.entityLiving.boundingBox
                    )
                )

                players.forEach {
                    if (CoronavirusAPI.getMetaIsInitiallyInfected(it.name.string)) {
                        withChance(params.infectedMobInfectEntityChanceParam) {
                            logger.info("AAA")
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
                                CoronavirusAPI.processInfectInitiator(event.entityLiving)
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
        val name = player.name.string
        if (!CoronavirusAPI.getMetaIsLoggedIn(name)) {
            CoronavirusAPI.getPlayerMeta(name)?.loggedIn = true

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
            player.world.playSound(
                player,
                player.position,
                SoundEvents.ENTITY_WITHER_SPAWN,
                SoundCategory.AMBIENT,
                0.5f,
                1.0f
            )
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

    private var epidemicWorldTicks = 0

    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        epidemicWorldTicks += 1
        if (epidemicWorldTicks == epidemicChanceChecksEveryTicks) {
            epidemicWorldTicks = 0

            val params = CoronavirusAPI.getCoronavirusParameters(
                event.world.difficulty.id
            )

            withChance(params.epidemicChanceParam) {
                CoronavirusTemporaryState.epidemic = true
                CoronavirusAPI.getCoronavirus().epidemics++
                // todo: send message to all players on server about it.
            }
        }
    }

    private val virusPlayerTicks = HashMap<String, Int>()
    private val virusKillingPlayerTicks = HashMap<String, Int>()

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        val name = event.player.name.string

        virusKillingPlayerTicks[name] = virusKillingPlayerTicks[name]?.plus(1) ?: 0
        if (CoronavirusAPI.getMetaIsKilling(name)) {
            if (virusKillingPlayerTicks[name] == playerKillingDamageEveryTicks) {
                virusKillingPlayerTicks[name] = 0

                if (event.player.isLiving) {
                    if (CoronavirusAPI.getMetaKillingTicks(name) >= pardonKillingTicks) {
                        val infectPercent = rndDoubleDecrementOrIncrement(
                            pardonInfectPercent, pardonInfectPercentModifier
                        )
                        // todo: send message `you cool, take a chance!`
                        CoronavirusAPI.setInfectPercent(name, infectPercent)
                        CoronavirusAPI.refreshPlayerKillingTick(name)
                        handlePlayerVirusState(event.player)
                    } else {
                        event.player.attackEntityFrom(covidDamageSource, playerDyingDamagePerSecond)
                        CoronavirusAPI.addPlayerKillingTick(name)
                        // todo: send message what player killing
                    }
                }
            }
            return
        }

        virusPlayerTicks[name] = virusPlayerTicks[name]?.plus(1) ?: 0
        if (virusPlayerTicks[name] == playerVirusChecksEveryTicks) {
            virusPlayerTicks[name] = 0

            val params = CoronavirusAPI.getCoronavirusParameters(
                event.player.world.difficulty.id
            )

            // todo: create minimal time cooldown for chance checking. (e.g 1 - 2min) and decrement time by difficulty.
            withChance(params.playerVirusEffectChanceParam) {
                CoronavirusAPI.getCoronavirusEffectByPercent(
                    CoronavirusAPI.getInfectPercent(name)
                ).apply(event.player)
            }

            handlePlayerVirusState(event.player)

            if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Recession) {
                CoronavirusAPI.disinfectPlayer(name, params.genericDisinfectDosePerTickParam)
            }
            if (CoronavirusAPI.getInfectStatus(name) == CoronavirusInfectStatus.Actively) {
                CoronavirusAPI.infectPlayer(name, params.genericInfectDosePerTickParam)
            }
        }
    }

    fun handlePlayerVirusState(player: PlayerEntity) {
        val name = player.name.string
        if (CoronavirusTemporaryState.stateMap[name] != null) {
            CoronavirusTemporaryState.stateMap[name]?.add(CoronavirusAPI.getInfectPercent(name))
        } else {
            CoronavirusTemporaryState.stateMap[name] = hashSetOf()
        }

        val equalsDelta = 0.0001
        val values = CoronavirusTemporaryState.stateMap[name]!!

        CoronavirusAPI.getPlayer(name)?.infectStatus = when {
            abs(values.last() - values.elementAt(values.count() - 2)) < equalsDelta -> CoronavirusInfectStatus.Suspended
            values.last() > values.elementAt(values.count() - 2) -> CoronavirusInfectStatus.Actively
            values.last() < values.elementAt(values.count() - 2) -> CoronavirusInfectStatus.Recession
            else -> CoronavirusInfectStatus.Suspended
        }
    }

    @SubscribeEvent
    fun onPlayerEating(event: LivingEntityUseItemEvent.Start) {
        if (event.entityLiving is ServerPlayerEntity) {
            if (
                event.item.item == Items.BEEF ||
                event.item.item == Items.CHICKEN ||
                event.item.item == Items.SPIDER_EYE ||
                event.item.item == Items.RABBIT ||
                event.item.item == Items.MUTTON ||
                event.item.item == Items.SALMON ||
                event.item.item == Items.COD
            ) onInfectedMeatEating(event)
            onHealingEatEating(event)
        }
    }

    private fun onPlayerDeath(event: LivingDeathEvent) {
        if (event.entityLiving is ServerPlayerEntity) {
            if (event.source.damageType == "covid19") {
                val player = event.entityLiving as ServerPlayerEntity
                val server = player.server
                server.playerList.bannedPlayers.addEntry(
                    ProfileBanEntry(
                        player.gameProfile,
                        null,
                        "Coronavirus Patrol",
                        null,
                        TranslationTextComponent(
                            "covid.ban.reason"
                        ).unformattedComponentText
                    )
                )
                player.connection.disconnect(
                    TranslationTextComponent("covid.player.died")
                )
            }
        }
    }

    private fun onHealingEatEating(event: LivingEntityUseItemEvent.Start) {
        val params = CoronavirusAPI.getCoronavirusParameters(
            event.entity.world.difficulty.id
        )
        val player = event.entityLiving as ServerPlayerEntity
        var healPercentage = 0.0

        when (event.item.item) {
            Items.GOLDEN_APPLE -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingGoldenAppleHealDoseParam,
                healingGoldenAppleHealDoseFloat
            )
            Items.ENCHANTED_GOLDEN_APPLE -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingEnchantedGoldenAppleHealDoseParam,
                healingEnchantedGoldenAppleHealDoseFloat
            )
            Items.GOLDEN_CARROT -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingGoldenCarrotHealDoseParam,
                healingGoldenCarrotHealDoseFloat
            )
            Potions.HEALING -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingPotionHealDoseParam,
                healingPotionHealDoseFloat
            )
            Potions.STRONG_HEALING -> healPercentage = rndDoubleDecrementOrIncrement(
                params.healingStrongPotionHealDoseParam,
                healingStrongPotionHealDoseFloat
            )
        }
        CoronavirusAPI.disinfectPlayer(player.name.string, healPercentage)
    }

    private fun onInfectedMeatEating(event: LivingEntityUseItemEvent.Start) {
        val params = CoronavirusAPI.getCoronavirusParameters(
            event.entity.world.difficulty.id
        )
        val player = event.entityLiving as ServerPlayerEntity

        if (CoronavirusAPI.getMetaIsInitiallyInfected(player.name.string)) {
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
