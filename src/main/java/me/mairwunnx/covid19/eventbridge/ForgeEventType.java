package me.mairwunnx.covid19.eventbridge;

public enum ForgeEventType {
    CommonSetup,
    DedicatedServerSetup,
    EnqueueIMC,
    ProcessIMC,
    DoClientStuff,
    LoadComplete,
    ModIdMapping,
    GatherData
}
