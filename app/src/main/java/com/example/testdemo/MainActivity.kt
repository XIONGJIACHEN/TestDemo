package com.example.testdemo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testdemo.adapter.WalletAdapter
import com.example.testdemo.repository.WalletRepository
import com.example.testdemo.util.formatCurrencyMoneyLabel
import com.example.testdemo.viewmodel.WalletViewModel
import com.example.testdemo.viewmodel.WalletViewModelFactory
import kotlinx.coroutines.launch
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private val viewModel: WalletViewModel by viewModels { 
        WalletViewModelFactory(WalletRepository(applicationContext)) 
    }
    private val walletAdapter = WalletAdapter()
    
    private lateinit var currencySpinner: Spinner
    private lateinit var totalBalanceValue: TextView
    private lateinit var walletRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupUI()
        observeData()
        
        // 启动模拟汇率更新
        viewModel.mockUpdateExchangeRates()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 停止模拟更新
        viewModel.stopMockUpdate()
    }
    
    private fun initViews() {
        currencySpinner = findViewById(R.id.currencySpinner)
        totalBalanceValue = findViewById(R.id.totalBalanceValue)
        walletRecyclerView = findViewById(R.id.walletRecyclerView)
    }

    private fun setupUI() {
        walletRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = walletAdapter
            setHasFixedSize(true)
            itemAnimator = null
        }

        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val currency = parent?.getItemAtPosition(position) as? String
                viewModel.setSelectedCurrency(currency ?: "")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.availableCurrencies.collect { currencies ->
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item,
                    currencies.toList()
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                currencySpinner.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.totalBalance.collect { total ->
                totalBalanceValue.text = formatCurrency(
                    total,
                    viewModel.selectedCurrency.value
                )
            }
        }

        lifecycleScope.launch {
            viewModel.walletItems.collect { items ->
                walletAdapter.submitList(items)
            }
        }
    }

    private fun formatCurrency(amount: BigDecimal, currency: String): String {
        return "${currency.formatCurrencyMoneyLabel()} ${amount.toPlainString()}"
    }
}