package com.yourname.modid;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    /*
        You also can add register new method and calling from this
        kotlin functions or something.
     */

    @SubscribeEvent
    public static void onBlocksRegistry(
        final RegistryEvent.Register<Block> event
    ) {
        /*
            You can do something here, or just call kotlin function from this.
         */
        LogManager.getLogger().info("HELLO from Register Blocks from JAVA!");
        EntryPoint.RegistryEvents.INSTANCE.onBlocksRegistry(event);
    }

    @SubscribeEvent
    public static void onItemsRegistry(
        final RegistryEvent.Register<Item> event
    ) {
        LogManager.getLogger().info("HELLO from Register Items from JAVA!");
        EntryPoint.RegistryEvents.INSTANCE.onItemsRegistry(event);
    }

    /*
        One more example:

        import net.minecraft.potion.Effect;

        @SubscribeEvent
        public static void onEffectsRegistry(
            final RegistryEvent.Register<Effect> event
        ) {
            LogManager.getLogger().info("HELLO from Register Effects from JAVA!");
            EntryPoint.RegistryEvents.INSTANCE.onEffectsRegistry(event);
        }
     */
}
