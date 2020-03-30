package com.yourname.modid.eventbridge;

import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

public interface IRunnable extends Runnable {
    void run(ModLifecycleEvent event);

    @Override
    default void run() {
    }
}
