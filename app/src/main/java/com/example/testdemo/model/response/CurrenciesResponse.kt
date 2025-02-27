package com.example.testdemo.model.response

import com.example.testdemo.model.Currency
import com.google.gson.annotations.SerializedName

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class CurrenciesResponse(
    @SerializedName("currencies")
    val currencies: List<Currency>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("ok")
    val ok: Boolean
) 