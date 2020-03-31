package me.mairwunnx.covid19;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(
        final RegistryEvent.Register<Block> event
    ) {
        EntryPoint.RegistryEvents.INSTANCE.onBlocksRegistry(event);
    }

    @SubscribeEvent
    public static void onItemsRegistry(
        final RegistryEvent.Register<Item> event
    ) {
        EntryPoint.RegistryEvents.INSTANCE.onItemsRegistry(event);
    }
}
