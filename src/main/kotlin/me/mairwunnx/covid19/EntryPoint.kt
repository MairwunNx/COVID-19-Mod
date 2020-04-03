@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package me.mairwunnx.covid19

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod

@Mod(value = "covid19")
internal class EntryPoint {
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
}
