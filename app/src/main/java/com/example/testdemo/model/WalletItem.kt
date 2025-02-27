package com.example.testdemo.model

import java.math.BigDecimal

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class WalletItem(
    val currency: Currency,
    val balance: BigDecimal,
    val convertedAmount: BigDecimal
) 