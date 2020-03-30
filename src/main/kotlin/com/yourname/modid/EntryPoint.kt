/*
    Suppressed some warnings because it redundant in this class.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER", "unused")

package com.yourname.modid

import com.yourname.modid.eventbridge.EventBridge
import com.yourname.modid.eventbridge.ForgeEventType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent.Register
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.InterModComms.IMCMessage
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import java.util.stream.Collectors


/**
 * The value here should match an entry in the
 * `META-INF/mods.toml` file.
 *
 * You can simplify expression to `@Mod("examplemod")`.
 */
@Mod(value = "examplemod")
internal class EntryPoint {
    /**
     * Directly reference a log4j logger.
     */
    private val logger = LogManager.getLogger()

    init {
        /**
         * Register ourselves for server and other game
         * events we are interested in.
         */
        MinecraftForge.EVENT_BUS.register(this)

        /**
         * Initializing event bridge for using forge
         * events from kotlin.
         */
        EventBridge.initialize()

        /**
         * Creating listeners on forge event via `EventBridge`.
         */
        EventBridge.addListener(
            this.javaClass, ForgeEventType.CommonSetup
        ) { setup(it as FMLCommonSetupEvent) }

        EventBridge.addListener(
            this.javaClass, ForgeEventType.DoClientStuff
        ) { doClientStuff(it as FMLClientSetupEvent) }

        EventBridge.addListener(
            this.javaClass, ForgeEventType.EnqueueIMC
        ) { enqueueIMC(it as InterModEnqueueEvent) }

        EventBridge.addListener(
            this.javaClass, ForgeEventType.ProcessIMC
        ) { processIMC(it as InterModProcessEvent) }
    }

    internal fun setup(event: FMLCommonSetupEvent) {
        /**
         * Some pre-init code.
         */
        logger.info("HELLO FROM PREINIT")
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.registryName)
    }

    internal fun doClientStuff(event: FMLClientSetupEvent) {
        /**
         * Do something that can only be done on the client.
         */
        logger.info("Got game settings {}", event.minecraftSupplier.get().gameSettings)
    }

    internal fun enqueueIMC(event: InterModEnqueueEvent) {
        /**
         * Some example code to dispatch IMC to another mod.
         */
        InterModComms.sendTo("examplemod", "helloworld") {
            logger.info("Hello world from the MDK")
            "Hello world"
        }
    }

    internal fun processIMC(event: InterModProcessEvent) {
        /**
         * Some example code to receive and process InterModComms
         * from other mods.
         */
        logger.info(
            "Got IMC ${event.imcStream.map { message: IMCMessage ->
                message.getMessageSupplier<Any>().get()
            }.collect(Collectors.toList())}"
        )
    }

    /**
     * You can use SubscribeEvent and let the Event Bus discover
     * methods to call.
     */
    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        /**
         * Do something when the server starts.
         */
        logger.info("HELLO from server starting")
    }

    /**
     * If you need add events you need see `[com.yourname.modid.RegistryEvents]` class in
     * package `com.yourname.modid` in `java` directory. (Contains
     * examples and some description).
     */
    object RegistryEvents {
        /**
         * This method called from [com.yourname.modid.RegistryEvents] from
         * onBlocksRegistry method. (:D It like small hack).
         */
        fun onBlocksRegistry(event: Register<Block>) {
            /**
             * Register a block here.
             */
            LogManager.getLogger().info("HELLO from Register Blocks from Kotlin!")
        }

        fun onItemsRegistry(event: Register<Item>) {
            /**
             * Register a item here.
             */
            LogManager.getLogger().info("HELLO from Register Items from Kotlin!")
        }
    }
}
