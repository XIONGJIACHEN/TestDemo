package com.example.testdemo.repository

import android.content.Context
import com.example.testdemo.model.Currency
import com.example.testdemo.model.ExchangeRate
import com.example.testdemo.model.WalletBalance
import com.example.testdemo.model.response.CurrenciesResponse
import com.example.testdemo.model.response.RatesResponse
import com.example.testdemo.model.response.WalletResponse
import com.google.gson.Gson

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 * 可以替换成从网络repo中取数据，目前是直接读本地数据
 */
class WalletRepository(private val context: Context) {
    private val gson = Gson()
    
    fun getCurrencies(): List<Currency> {
        return try {
            val json = context.assets.open("currencies.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, CurrenciesResponse::class.java)?.currencies ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun getWalletBalances(): List<WalletBalance> {
        return try {
            val json = context.assets.open("wallet-balance.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, WalletResponse::class.java)?.wallet ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun getExchangeRates(): List<ExchangeRate> {
        return try {
            val json = context.assets.open("live-rates.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, RatesResponse::class.java)?.tiers ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 