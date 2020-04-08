package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.coronavirusFinalizedExperience
import me.mairwunnx.covid19.api.coronavirusFinalizedExperienceModifier
import me.mairwunnx.covid19.api.playSound
import me.mairwunnx.covid19.virusSuspendedSound
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.entity.item.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.Effects
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import kotlin.math.roundToInt

object ItemDropHandler {
    private val conditionsItemsHashMap = hashMapOf<BlockPos, MutableList<ItemEntity>>()
    private val conditionsHashMap = hashMapOf<BlockPos, MutableList<String>>()

    // Default value is `2` because is commonly using difficulty in game :D.
    private var cachedDifficulty = 2
    private var cachedItems = getRequiredResources(cachedDifficulty).map {
        it.registryName.toString()
    }

    fun purgeConditionsItemMaps() {
        conditionsItemsHashMap.clear()
        conditionsHashMap.clear()
    }

    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerItemDrop(event: ItemTossEvent) {
        if (!CoronavirusAPI.isCoronavirusFinalized()) {
            if (cachedDifficulty != event.player.world.difficulty.id) {
                cachedDifficulty = event.player.world.difficulty.id
                cachedItems = getRequiredResources(event.player.world.difficulty.id).map {
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

                if (cachedItems.count() <= conditionsHashMap[event.entityItem.position]?.count() ?: 0) {
                    val list = conditionsHashMap[event.entityItem.position] ?: mutableListOf()

                    val sortedDroppedItems = list.sorted()
                    val sortedRequestedItems = cachedItems.sorted()

                    if (sortedDroppedItems == sortedRequestedItems) {
                        repeat(4) {
                            val lightning = LightningBoltEntity(
                                event.player.world,
                                getItemPos(event.entityItem.position).x.toDouble(),
                                getItemPos(event.entityItem.position).y.toDouble(),
                                getItemPos(event.entityItem.position).z.toDouble(),
                                true
                            )
                            (event.player.world as ServerWorld).addLightningBolt(lightning)
                            event.player.onStruckByLightning(lightning)
                        }
                        CoronavirusAPI.getCoronavirus().finalized = true
                        event.player.server?.playerList?.sendMessage(
                            TranslationTextComponent(
                                "covid19.suspended_by",
                                event.player.name.string
                            )
                        )
                        getRewardResources(cachedDifficulty).forEach {
                            event.player.addItemStackToInventory(it.defaultInstance)
                        }
                        event.player.addExperienceLevel(getRewardExperience(cachedDifficulty))
                        event.player.world.players.forEach {
                            playSound(
                                player = it,
                                soundEvent = virusSuspendedSound,
                                soundCategory = SoundCategory.AMBIENT
                            )
                        }
                        getRewardEffects(cachedDifficulty).forEach { event.player.addPotionEffect(it) }
                        conditionsItemsHashMap[event.entityItem.position]?.forEach { it.remove() }
                        conditionsHashMap.clear()
                        conditionsItemsHashMap.clear()
                    }
                }
            }
        }
    }

    private fun getItemPos(position: BlockPos) =
        conditionsItemsHashMap[position]?.first()?.position ?: position

    private fun getRewardExperience(difficulty: Int): Int {
        var exp = coronavirusFinalizedExperience
        repeat(difficulty) { exp = (exp * coronavirusFinalizedExperienceModifier).roundToInt() }
        return exp
    }

    private fun getRewardResources(difficulty: Int): List<Item> {
        val items = mutableListOf<Item>()
        when (difficulty) {
            0 -> {
                repeat(2) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(4) { items.add(Items.DIAMOND) }
            }
            1 -> {
                repeat(4) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(2) { items.add(Items.EMERALD) }
                repeat(4) { items.add(Items.DIAMOND) }
            }
            2 -> {
                repeat(8) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(4) { items.add(Items.EMERALD) }
                repeat(6) { items.add(Items.DIAMOND) }
            }
            3 -> {
                repeat(16) { items.add(Items.ENCHANTED_GOLDEN_APPLE) }
                repeat(8) { items.add(Items.EMERALD) }
                repeat(16) { items.add(Items.DIAMOND) }
            }
        }
        return items
    }

    private fun getRewardEffects(difficulty: Int): List<EffectInstance> {
        val effects = mutableListOf<EffectInstance>()
        if (difficulty >= 0) effects.add(EffectInstance(Effects.SATURATION, 6000, 2))
        if (difficulty >= 1) effects.add(EffectInstance(Effects.FIRE_RESISTANCE, 12000, 2))
        if (difficulty >= 2) effects.add(EffectInstance(Effects.STRENGTH, 9000, 2))
        if (difficulty >= 2) effects.add(EffectInstance(Effects.REGENERATION, 4500, 2))
        return effects
    }

    private fun getRequiredResources(difficulty: Int): List<Item> {
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
