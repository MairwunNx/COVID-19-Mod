@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.playSound
import me.mairwunnx.covid19.handlers.*
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.entity.item.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod(value = "covid19")
internal class EntryPoint {
    private val coronavirusSuspendedMessage = TranslationTextComponent("covid19.suspended")

    init {
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(EntitySpawnHandler)
        MinecraftForge.EVENT_BUS.register(WorldTickHandler)
        MinecraftForge.EVENT_BUS.register(PlayerJoinHandler)
        MinecraftForge.EVENT_BUS.register(PlayerEatHandler)
        MinecraftForge.EVENT_BUS.register(PlayerDeathHandler)
        MinecraftForge.EVENT_BUS.register(ServerEventHandler)
        MinecraftForge.EVENT_BUS.register(EntityMoveHandler)
        MinecraftForge.EVENT_BUS.register(PlayerTickHandler)
    }

    val conditionsItemsHashMap = hashMapOf<BlockPos, MutableList<ItemEntity>>()
    val conditionsHashMap = hashMapOf<BlockPos, MutableList<String>>()

    private var cachedDifficulty = 2
    private var cachedItems = getResources(cachedDifficulty).map {
        it.registryName.toString()
    }

    @SubscribeEvent
    fun test(event: ItemTossEvent) {
        if (!CoronavirusAPI.isCoronavirusFinalized()) {
            if (cachedDifficulty != event.player.world.difficulty.id) {
                cachedDifficulty = event.player.world.difficulty.id
                cachedItems = getResources(event.player.world.difficulty.id).map {
                    it.registryName.toString()
                }
            }

            // Clearing collection to avoid hard memory using.
            if (conditionsHashMap.keys.count() > 64) conditionsHashMap.clear()
            if (conditionsItemsHashMap.keys.count() > 64) conditionsHashMap.clear()

            val itemName = event.entityItem.item.item.registryName.toString()
            if (itemName in cachedItems) {
                if (conditionsHashMap[event.entityItem.position] == null) {
                    conditionsHashMap[event.entityItem.position] = mutableListOf()
                }
                if (conditionsItemsHashMap[event.entityItem.position] == null) {
                    conditionsItemsHashMap[event.entityItem.position] = mutableListOf()
                }
                conditionsHashMap[event.entityItem.position]?.add(itemName)
                conditionsItemsHashMap[event.entityItem.position]?.add(event.entityItem)

//                val chickens = event.entityItem.world.getEntitiesWithinAABB(
//                    ChickenEntity::class.java, AxisAlignedBB(event.entityItem.position)
//                )

//                if (chickens.isNotEmpty()) {
                if (cachedItems.count() <= conditionsHashMap[event.entityItem.position]?.count() ?: 0) {
                    val list = conditionsHashMap[event.entityItem.position] ?: mutableListOf()

                    val sortedDroppedItems = list.sorted()
                    val sortedRequestedItems = cachedItems.sorted()

                    if (sortedDroppedItems == sortedRequestedItems) {
                        repeat(4) {
                            val lightning = LightningBoltEntity(
                                event.player.world,
                                event.entityItem.position.x.toDouble(),
                                event.entityItem.position.y.toDouble(),
                                event.entityItem.position.z.toDouble(),
                                true
                            )
                            (event.player.world as ServerWorld).addLightningBolt(lightning)
                            event.player.onStruckByLightning(lightning)
                        }
                        CoronavirusAPI.getCoronavirus().finalized = true
                        event.player.sendMessage(coronavirusSuspendedMessage)
                        // todo: send reward to player (experience (depends on difficulty), 16 || 8 || 4 || 2 enchanted golden apples)
                        // todo: broadcast all players about coronavirus suspending by player
                        event.player.world.players.forEach {
                            playSound(
                                player = it,
                                soundEvent = SoundEvents.ENTITY_ENDER_DRAGON_DEATH,
                                soundCategory = SoundCategory.AMBIENT
                            )
                        }
                        conditionsItemsHashMap[event.entityItem.position]?.forEach { it.remove() }
                        conditionsHashMap.clear()
                        conditionsItemsHashMap.clear()
                    }
                }
//                }
            }
        }
    }

    private fun getResources(difficulty: Int): List<Item> {
        val items = mutableListOf<Item>()
        when (difficulty) {
            0 -> repeat(2) { items.add(Items.EMERALD) }
            1 -> {
                repeat(2) { items.add(Items.EMERALD) }
                repeat(3) { items.add(Items.DIAMOND) }
            }
            2 -> {
                repeat(1) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(3) { items.add(Items.EMERALD) }
                repeat(3) { items.add(Items.DIAMOND) }
            }
            3 -> {
                repeat(2) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(3) { items.add(Items.EMERALD) }
                repeat(6) { items.add(Items.DIAMOND) }
            }
        }
        return items
    }
}
