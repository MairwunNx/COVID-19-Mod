package me.mairwunnx.covid19.handlers

import me.mairwunnx.covid19.api.CoronavirusAPI
import me.mairwunnx.covid19.api.isEvenNumber
import me.mairwunnx.covid19.api.playSound
import me.mairwunnx.covid19.api.playerJoinLightningRowDistance
import me.mairwunnx.covid19.api.store.CoronavirusModel
import me.mairwunnx.covid19.welcomeMessage
import me.mairwunnx.covid19.welcomeSound
import net.minecraft.entity.effect.LightningBoltEntity
import net.minecraft.util.SoundCategory
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor

object PlayerJoinHandler {
    @SubscribeEvent
    @Suppress("unused")
    fun onFirstPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.player
        val name = player.name.string

        if (CoronavirusAPI.isPlayerDead(name)) {
            DistExecutor.runWhenOn(Dist.CLIENT) {
                throw IllegalStateException(
                    "\n\n       Your world no longer available! LOSER! You was killed by Coronavirus!".plus(
                        "\n\n           Hmm, description, well. You died of a virus; mom and dad will not give you a second life.\n\n   If you really want to continue playing in this world, then look at the `saves/<you world here>` folder, explore.\n   If something goes wrong, write to Mr. @MairwunNx.\n\n      - Also read this https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_pandemic\n      - And this: STAY HOME.SAVE LIVES. https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public\n\n      - Берегите себя и своих близких.\n"
                    )
                )
            }
        }

        if (!CoronavirusAPI.getMetaIsLoggedIn(name)) {
            CoronavirusAPI.addPlayerData(CoronavirusModel.Player(name))

            if (!CoronavirusAPI.isCoronavirusFinalized()) {
                repeat(2) { row ->
                    for (lightning in 1..4) {
                        val lightningInstance = LightningBoltEntity(
                            player.world,
                            when {
                                !isEvenNumber(lightning) -> getLightningPosByRow(
                                    player.positionVec.x, row, lightning
                                )
                                else -> player.positionVec.x
                            },
                            player.positionVec.y,
                            when {
                                isEvenNumber(lightning) -> getLightningPosByRow(
                                    player.positionVec.z, row, lightning
                                )
                                else -> player.positionVec.z
                            },
                            true
                        )
                        (player.world as ServerWorld).addLightningBolt(lightningInstance)
                        player.onStruckByLightning(lightningInstance)
                    }
                }

                player.sendMessage(welcomeMessage)
                playSound(
                    player = player,
                    soundEvent = welcomeSound,
                    soundCategory = SoundCategory.AMBIENT,
                    ignoreWorld = true
                )
            }
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
