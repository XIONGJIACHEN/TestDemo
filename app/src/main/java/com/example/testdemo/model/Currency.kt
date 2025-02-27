package com.example.testdemo.model

import com.google.gson.annotations.SerializedName

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class Currency(
    @SerializedName("coin_id")
    val coinId: String,
    val name: String,
    val symbol: String,
    @SerializedName("token_decimal")
    val tokenDecimal: Int,
    @SerializedName("token_decimal_value")
    val tokenDecimalValue: String,
    @SerializedName("display_decimal")
    val displayDecimal: Int,
    @SerializedName("colorful_image_url")
    val colorfulImageUrl: String
) 