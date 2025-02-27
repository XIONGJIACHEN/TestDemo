package com.example.testdemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testdemo.model.ExchangeRate
import com.example.testdemo.model.WalletItem
import com.example.testdemo.repository.WalletRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * Created by xiongjiachen on 2025/2/27
 * @author xiongjiachen@bytedance.com
 */
class WalletViewModel(private val repository: WalletRepository) : ViewModel() {
    private val _selectedCurrency = MutableStateFlow("USD")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _totalBalance = MutableStateFlow(BigDecimal.ZERO)
    val totalBalance = _totalBalance.asStateFlow()

    private val _walletItems = MutableStateFlow<List<WalletItem>>(emptyList())
    val walletItems = _walletItems.asStateFlow()

    private val _availableCurrencies = MutableStateFlow<Set<String>>(emptySet())
    val availableCurrencies = _availableCurrencies.asStateFlow()

    // 缓存当前使用的汇率数据
    private var currentRates: List<ExchangeRate> = emptyList()

    private var mockJob: Job? = null

    init {
        loadData()
    }

    fun setSelectedCurrency(currency: String) {
        _selectedCurrency.value = currency
        calculateBalances()
    }

    //demo中数据直接从本地加载，实际开发中可能需要从网络获取
    private fun loadData() {
        viewModelScope.launch {
            // 首次加载使用repository中的汇率
            currentRates = repository.getExchangeRates()
            // 收集可用的目标货币
            updateAvailableCurrencies(currentRates)

            calculateBalances()
        }
    }

    /**
     * 更新实时汇率并重新计算余额
     * @param newRates 从网络获取的最新汇率
     */
    private fun updateExchangeRates(newRates: List<ExchangeRate>) {
        viewModelScope.launch {
            // 更新当前使用的汇率数据
            currentRates = newRates

            // 更新可用货币并处理选中货币
            updateAvailableCurrencies(newRates)

            // 使用新的汇率重新计算余额
            calculateBalances()
        }
    }

    /**
     * mock 汇率更新逻辑
     * 每10秒随机波动汇率(0.8-1.2之间)
     */
    fun mockUpdateExchangeRates() {
        mockJob?.cancel()

        mockJob = viewModelScope.launch {
            while (true) {
                // 获取基础汇率数据
                val baseRates = repository.getExchangeRates()

                // 计算波动后的新汇率
                val newRates = baseRates.map { exchangeRate ->
                    exchangeRate.copy(
                        rates = exchangeRate.rates.map { rate ->
                            // 生成0.8到1.2之间的随机系数
                            val randomFactor = BigDecimal(Random.nextDouble(0.8, 1.2)
                                .toString())
                                .setScale(6, RoundingMode.HALF_UP)
                            
                            // 对每个汇率随机波动
                            rate.copy(
                                rate = rate.rate.multiply(randomFactor)
                                    .setScale(6, RoundingMode.HALF_UP)
                            )
                        }
                    )
                }

                // 更新汇率
                updateExchangeRates(newRates)

                Log.d(TAG, "Mock rates updated with random factor ")

                // 延迟10秒
                delay(2000)
            }
        }
    }

    /**
     * 停止模拟汇率更新
     */
    fun stopMockUpdate() {
        mockJob?.cancel()
        mockJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopMockUpdate()
    }

    /**
     * 更新可用货币列表并处理选中货币
     * 如果新汇率中包含当前选中的货币则保持不变，否则选择第一个可用货币
     */
    private fun updateAvailableCurrencies(rates: List<ExchangeRate>) {
        val newAvailableCurrencies = rates.map { it.toCurrency }.toSet()
        _availableCurrencies.value = newAvailableCurrencies

        // 如果当前选中的货币不在新的可用货币列表中，选择第一个可用货币
        if (!newAvailableCurrencies.contains(_selectedCurrency.value)) {
            _selectedCurrency.value = newAvailableCurrencies.firstOrNull() ?: "USD"
        }
        // 如果当前选中的货币在新的列表中，则保持不变
    }

    private fun calculateBalances() {
        viewModelScope.launch {
            val currencies = repository.getCurrencies()
            val balances = repository.getWalletBalances()
            
            // 保持原有列表顺序，只更新convertedAmount
            val currentItems = _walletItems.value
            val newItems = if (currentItems.isEmpty()) {
                // 首次加载使用完整构建，过滤掉找不到Currency的项
                balances.mapNotNull { balance ->
                    //有没有可能存在balance里面有数值，但是在currency中找不到？ 目前如果出现这种情况直接不展示该item
                    val currency = currencies.find { it.symbol == balance.currency } ?: return@mapNotNull null
                    val rate = findApplicableRate(currentRates, balance.currency, _selectedCurrency.value, balance.amount)
                    
                    WalletItem(
                        currency = currency,
                        balance = balance.amount,
                        convertedAmount = balance.amount.multiply(rate)
                    )
                }
            } else {
                // 后续更新保持原有顺序
                currentItems.map { item ->
                    val rate = findApplicableRate(currentRates, item.currency.symbol, _selectedCurrency.value, item.balance)
                    item.copy(
                        convertedAmount = item.balance.multiply(rate)
                    )
                }
            }
            
            _walletItems.value = newItems
            _totalBalance.value = newItems.sumOf { it.convertedAmount }
        }
    }

    // 猜测多个汇率 是有汇率区间，不确定 先这样实现
    private fun findApplicableRate(
        rates: List<ExchangeRate>,
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ): BigDecimal {
        val exchangeRate = rates.find {
            it.fromCurrency == fromCurrency && it.toCurrency == toCurrency
        } ?: return BigDecimal.ZERO
        
        return exchangeRate.rates
            .sortedBy { it.amount }
            .find { it.amount >= amount }
            ?.rate
            ?: exchangeRate.rates.last().rate
    }

    companion object {
        private const val TAG = "wallet_vm"
    }
}