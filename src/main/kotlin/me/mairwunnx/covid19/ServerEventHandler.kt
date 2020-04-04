package me.mairwunnx.covid19

import me.mairwunnx.covid19.api.purgeVirusStateMap
import me.mairwunnx.covid19.api.store.CoronavirusStore
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import java.io.File

@Suppress("unused")
object ServerEventHandler {
    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) = initializeCoronavirusStore(event)

    @SubscribeEvent
    fun onServerSaving(
        @Suppress("UNUSED_PARAMETER") event: WorldEvent.Save
    ) = CoronavirusStore.save()

    @SubscribeEvent
    fun onServerShuttingDown(
        @Suppress("UNUSED_PARAMETER") event: FMLServerStoppingEvent
    ) {
        CoronavirusStore.save()
        purgeVirusStateMap()
    }

    private fun initializeCoronavirusStore(event: FMLServerStartingEvent) {
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable {
                // todo: bug: world names with same names
                //  (e.g: `survival`, but have duplicates in file system and placed in folder
                //  with suffix `(1)` incorrectly worked. Because we get world name, not world
                //  folder path).
                CoronavirusStore.init("saves${File.separator}${event.server.worldName}")
            }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable {
                CoronavirusStore.init(event.server.folderName)
            }
        }
    }
}
