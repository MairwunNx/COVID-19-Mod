package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.management.ProfileBanEntry
import net.minecraft.util.text.TranslationTextComponent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PlayerDeathHandler {
    private var reasonMessage = TranslationTextComponent(
        "covid.ban.reason"
    ).unformattedComponentText
    val diedMessage = TranslationTextComponent("covid.player.died")

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
                        reasonMessage
                    )
                )
                player.connection.disconnect(diedMessage)
            }
        }
    }
}
