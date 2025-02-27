package com.example.testdemo.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class WalletBalance(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("amount")
    val amount: BigDecimal
) 