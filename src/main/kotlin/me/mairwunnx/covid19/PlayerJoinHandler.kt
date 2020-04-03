package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.isEvenNumber
import me.mairwunnx.covid19.api.playSound
import me.mairwunnx.covid19.api.playerJoinLightningRowDistance
import me.mairwunnx.covid19.api.store.CoronavirusModel
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object PlayerJoinHandler {
    private val welcomeMessage = TranslationTextComponent("covid19.welcome")

    @SubscribeEvent
    @Suppress("unused")
    fun onFirstPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.player
        val name = player.name.string

        if (!CoronavirusAPI.getMetaIsLoggedIn(name)) {
            CoronavirusAPI.addPlayerData(CoronavirusModel.Player(name))

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

            player.sendMessage(welcomeMessage)
            player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 0.5f, 1.0f)
            playSound(player, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.AMBIENT)
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
}
