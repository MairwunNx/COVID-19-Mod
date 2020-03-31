package me.mairwunnx.covid19.eventbridge;

import kotlin.Pair;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EventBridge {
    private static HashMap<ForgeEventType, List<Pair<Class<?>, IRunnable>>> eventHashMap = new HashMap<>();

    public static void addListener(
        @NotNull Class<?> instance,
        @NotNull ForgeEventType type,
        @NotNull IRunnable runnable
    ) {
        putEvent(type, new Pair<>(instance, runnable));
    }

    private static void putEvent(ForgeEventType type, Pair<Class<?>, IRunnable> data) {
        List<Pair<Class<?>, IRunnable>> eventList = eventHashMap.get(type);
        if (eventList != null) {
            eventList.add(new Pair<>(data.getFirst(), data.getSecond()));
        } else {
            eventHashMap.put(
                type, Collections.singletonList(new Pair<>(data.getFirst(), data.getSecond()))
            );
        }
    }

    private static List<Pair<Class<?>, IRunnable>> getEvents(ForgeEventType forType) {
        if (eventHashMap.get(forType) == null) {
            return Collections.emptyList();
        }
        return eventHashMap.get(forType);
    }

    public static void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::dedicatedServerSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::loadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::modIdMapping);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::gatherData);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        getEvents(ForgeEventType.CommonSetup).forEach(data -> data.getSecond().run(event));
    }

    private static void dedicatedServerSetup(final FMLDedicatedServerSetupEvent event) {
        getEvents(ForgeEventType.DedicatedServerSetup).forEach(data -> data.getSecond().run(event));
    }

    private static void loadComplete(final FMLLoadCompleteEvent event) {
        getEvents(ForgeEventType.LoadComplete).forEach(data -> data.getSecond().run(event));
    }

    private static void modIdMapping(final FMLModIdMappingEvent event) {
        getEvents(ForgeEventType.ModIdMapping).forEach(data -> data.getSecond().run(event));
    }

    private static void gatherData(final GatherDataEvent event) {
        getEvents(ForgeEventType.GatherData).forEach(data -> data.getSecond().run(event));
    }

    private static void doClientStuff(final FMLClientSetupEvent event) {
        getEvents(ForgeEventType.DoClientStuff).forEach(data -> data.getSecond().run(event));
    }

    private static void enqueueIMC(final InterModEnqueueEvent event) {
        getEvents(ForgeEventType.EnqueueIMC).forEach(data -> data.getSecond().run(event));
    }

    private static void processIMC(final InterModProcessEvent event) {
        getEvents(ForgeEventType.ProcessIMC).forEach(data -> data.getSecond().run(event));
    }
}
