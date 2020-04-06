package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.banReasonMessage
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.management.ProfileBanEntry
import net.minecraft.util.text.TextComponentUtils
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PlayerDeathHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onPlayerDeath(event: LivingDeathEvent) {
        if (event.entityLiving is ServerPlayerEntity) {
            if (event.source.damageType == "covid19") {
                val player = event.entityLiving as ServerPlayerEntity
                val server = player.server

                CoronavirusAPI.getPlayer(player.name.string)?.isDead = true
                server.playerList.bannedPlayers.addEntry(
                    ProfileBanEntry(
                        player.gameProfile,
                        null,
                        "Coronavirus Patrol",
                        null,
                        banReasonMessage
                    )
                )
                player.connection.disconnect(TextComponentUtils.toTextComponent { banReasonMessage })
            }
        }
    }
}
