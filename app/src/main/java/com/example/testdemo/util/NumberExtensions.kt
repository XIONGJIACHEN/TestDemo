package com.example.testdemo.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
//displayDecimal 展示的最大精度
fun BigDecimal.formatAmount(decimals: Int): String {
    val scaled = this.setScale(decimals, RoundingMode.HALF_UP)
    return scaled.stripTrailingZeros().toPlainString()
}

fun String.formatCurrencyMoneyLabel(): String {
//    return when (this) {
//        "USD" -> "$"
//        else -> this
//    }

    return "$"
}