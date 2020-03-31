package me.mairwunnx.covid19.api

import net.minecraft.util.math.AxisAlignedBB

inline fun withChance(chance: Double, action: () -> Unit) {
    if (Math.random() < chance) action.invoke()
}

fun expandAxisAlignedBB(
    size: Double,
    axisAlignedBB: AxisAlignedBB
): AxisAlignedBB {
    axisAlignedBB.expand(size, size, size)
    axisAlignedBB.expand(-size, -size, -size)
    return axisAlignedBB
}
