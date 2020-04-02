package me.mairwunnx.covid19.api

import net.minecraft.util.math.AxisAlignedBB
import java.util.concurrent.ThreadLocalRandom

inline fun withChance(chance: Double, action: () -> Unit) {
    if (Math.random() < chance) action.invoke()
}

fun rndDoubleDecrementOrIncrement(
    baseValue: Double, modifier: Double
): Double {
    val newModifier = randomBetweenZeroAnd(modifier)
    val result = ThreadLocalRandom.current().nextInt(0, 2)
    return if (result == 1) baseValue - newModifier else baseValue + newModifier
}

fun randomBetweenZeroAnd(
    other: Double
) = ThreadLocalRandom.current().nextDouble(0.0, other)

fun isEvenNumber(number: Int) = (number and 1) == 0

fun expandAxisAlignedBB(
    size: Double,
    axisAlignedBB: AxisAlignedBB
): AxisAlignedBB {
    axisAlignedBB.expand(size, size, size)
    axisAlignedBB.expand(-size, -size, -size)
    return axisAlignedBB
}
