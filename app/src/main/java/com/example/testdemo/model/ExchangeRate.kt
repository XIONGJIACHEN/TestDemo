package com.example.testdemo.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class ExchangeRate(
    @SerializedName("from_currency")
    val fromCurrency: String,
    @SerializedName("to_currency")
    val toCurrency: String,
    @SerializedName("rates")
    val rates: List<Rate>,
    @SerializedName("time_stamp")
    val timeStamp: Long
)

data class Rate(
    @SerializedName("amount")
    val amount: BigDecimal,
    @SerializedName("rate")
    val rate: BigDecimal
) 