package com.example.testdemo.model.response

import com.example.testdemo.model.WalletBalance
import com.google.gson.annotations.SerializedName
/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
data class WalletResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("warning")
    val warning: String,
    @SerializedName("wallet")
    val wallet: List<WalletBalance>
) 