package me.mairwunnx.covid19

import net.minecraft.util.text.TranslationTextComponent as messageOf

val welcomeMessage = messageOf("covid19.welcome")
val epidemicStartMessage = messageOf("covid19.epidemic.start")
val epidemicStopMessage = messageOf("covid19.epidemic.stop")
val playerLivedTakeAChanceMessage = messageOf("covid19.infect.take_a_chance")
val virusRebornMessage = messageOf("covid19.reborn")
var banReasonMessage: String = messageOf("covid19.ban.reason").unformattedComponentText
var infectedMessage = messageOf("covid19.infected")
var disinfectedMessage = messageOf("covid19.disinfected")
var disinfectedButSimpleMessage = messageOf("covid19.disinfected_but_simple")
